/*
 * Copyright 2017  Quentin Lefèvre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.qlefevre.crudadmin.web;

import static com.github.qlefevre.crudadmin.util.CrudAdminUtils.SDF;
import static com.github.qlefevre.crudadmin.util.CrudAdminUtils.defaultString;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.qlefevre.crudadmin.CrudAdminDefaultObjectIdSerializer;
import com.github.qlefevre.crudadmin.CrudAdminProperties;
import com.github.qlefevre.crudadmin.model.CrudAdminObject;
import com.github.qlefevre.crudadmin.model.CrudAdminObjectField;
import com.github.qlefevre.crudadmin.model.CrudAdminRepository;
import com.github.qlefevre.crudadmin.util.CrudAdminUtils;

@Controller
public class CrudAdminController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CrudAdminController.class);

	@Autowired
	private CrudAdminProperties crudAdminProperties;

	@Autowired
	private List<? extends PagingAndSortingRepository<?, ?>> repositories;

	private CrudAdminDefaultObjectIdSerializer idSerializer;

	private Map<String, CrudAdminRepository> repositoryMap;

	/**
	 * Initialize all properties after instantiation
	 */
	@PostConstruct
	public void postConstruct() {

		// Object Id Serializer
		if (!StringUtils.isEmpty(crudAdminProperties.getObjectidserializer())) {
			try {
				idSerializer = ((CrudAdminDefaultObjectIdSerializer) Class
						.forName(crudAdminProperties.getObjectidserializer()).newInstance());
			} catch (Exception ex) {
				LOGGER.error("Unable to create Object Id Serializer for class  "
						+ crudAdminProperties.getObjectidserializer());
			}
		}
		if (idSerializer == null) {
			idSerializer = new CrudAdminDefaultObjectIdSerializer();
		}

		// Map of PagingAndSortingRepository
		repositoryMap = new HashMap<>();
		if (repositories != null) {
			for (PagingAndSortingRepository<?, ?> repo : repositories) {
				CrudAdminRepository repoAdmin = CrudAdminUtils.getDomainClass(repo);
				repositoryMap.put(repoAdmin.getDomainTypeNameLowerCase(), repoAdmin);
			}
		}

	}

	/**
	 * Default page.
	 *
	 * @param model
	 *            current model
	 * @return list template name
	 */
	@RequestMapping(value = { "${crudadmin.url}", "${crudadmin.url}/" }, method = RequestMethod.GET)
	public String defaultPage(Model model) {
		String type = repositoryMap.values().iterator().next().getDomainTypeNameLowerCase();
		int defaultSizePage = crudAdminProperties.getDefaultpagesize();
		return listPage(type, defaultSizePage, 0, model);
	}

	/**
	 * List all elements.
	 *
	 * @param type
	 *            the domain type
	 * @param page
	 *            current page
	 * @param size
	 *            current page size
	 * @param model
	 *            current model
	 * @return list template name
	 */
	@RequestMapping(value = "${crudadmin.url}/list/{type}/{size}/{page}", method = RequestMethod.GET)
	public String listPage(@PathVariable String type, @PathVariable int size, @PathVariable int page, Model model) {
		CrudAdminRepository repoAdmin = repositoryMap.get(type);
		long count = repoAdmin.getRepository().count();
		int numberofpages = (int) count / size;
		page = page < 0 ? 0 : page >= numberofpages ? numberofpages : page;
		int paginationstart = Math.min(Math.max(0, page - 3), numberofpages);
		int paginationend = count < size ? 0 : Math.min(numberofpages, paginationstart + 6);
		Pageable pageable = new PageRequest(page, size);
		model.addAttribute("count", count);
		model.addAttribute("paginationstart", paginationstart);
		model.addAttribute("paginationend", paginationend);
		model.addAttribute("numberofpages", numberofpages);
		model.addAttribute("tableheaders", repoAdmin.getTableHeaders());
		model.addAttribute("objects", getObjects(repoAdmin, pageable));
		addAttributes(model, repoAdmin, page, size);
		return crudAdminProperties.getTemplatelist();
	}

	/**
	 * View a specific element by its id.
	 *
	 * @param type
	 *            the domain type
	 * @param id
	 *            element id
	 * @param page
	 *            current page
	 * @param size
	 *            current page size
	 * @param model
	 *            current model
	 * @return view template name
	 */
	@RequestMapping(value = "${crudadmin.url}/view/{type}/{size}/{page}/{id}/")
	public String show(@PathVariable String type, @PathVariable String id, @PathVariable int size,
			@PathVariable int page, Model model) {
		CrudAdminRepository repoAdmin = repositoryMap.get(type);
		addAttributes(model, repoAdmin, page, size);
		model.addAttribute("fields", getFields(repoAdmin, id));
		model.addAttribute("id",id);
		return crudAdminProperties.getTemplateview();
	}

	/**
	 * Edit a specific element by its id.
	 * 
	 * @param type
	 *            the domain type
	 * @param id
	 *            element id
	 * @param page
	 *            current page
	 * @param size
	 *            current page size
	 * @param model
	 *            current model
	 * @return edit template name
	 */
	@RequestMapping(value = "${crudadmin.url}/edit/{type}/{size}/{page}/{id}/")
	public String edit(@PathVariable String type, @PathVariable String id, @PathVariable int size,
			@PathVariable int page, Model model) {
		return doEdit(type, id, size, page, "", model);
	}

	/**
	 * New element.
	 *
	 * @param type
	 *            the domain type
	 * @param page
	 *            current page
	 * @param size
	 *            current page size
	 * @param model
	 *            current model
	 * @return edit template name
	 */
	@RequestMapping(value = "${crudadmin.url}/new/{type}/{size}/{page}")
	public String newPage(@PathVariable String type, @PathVariable int size, @PathVariable int page, Model model) {
		try {
			return doEdit(type, null, size, page, "", model);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Save element to database
	 * 
	 * @param type
	 *            the domain type
	 * @param request
	 *            current HttpServletRequest
	 * @param page
	 *            current page
	 * @param size
	 *            current page size
	 * @param model
	 *            current model
	 * 
	 * @return edit/view template name
	 */
	@RequestMapping(value = "${crudadmin.url}/post/{type}/{size}/{page}", method = RequestMethod.POST)
	public String save(@PathVariable String type, @PathVariable int size, @PathVariable int page,
			HttpServletRequest request, Model model) {
		try {
			CrudAdminRepository repoAdmin = repositoryMap.get(type);
			ServletRequestParameterPropertyValues values = new ServletRequestParameterPropertyValues(request);
			final BindingResult result = repoAdmin.getWebDataBinder(values).getBindingResult();
			Object object = result.getTarget();
			String idString = values.getPropertyValue("id").getValue().toString();
			idString = idString.isEmpty() ? null : idString;
			Object id = null;
			try {
				// existing object ?
				if (idString != null) {
					id = idSerializer.fromString(idString, repoAdmin.getIdType());
					repoAdmin.getId().set(object, id);
				}
				// save
				repoAdmin.getRepositoryObjectSerializable().save(object);
				idString =  getStringFromId(repoAdmin.getId().get(object), repoAdmin);			
				return "redirect:/" + crudAdminProperties.getUrl() + "/view/" + repoAdmin.getDomainTypeNameLowerCase()
						+ "/" + size + "/" + page + "/" + idString+'/';
			} catch (Exception transactionException) {
				Throwable rootCause = transactionException;
				while (rootCause.getCause() != null) {
					rootCause = rootCause.getCause();
				}
				if (rootCause instanceof ConstraintViolationException) {
					ConstraintViolationException constraintViolationException = (ConstraintViolationException) rootCause;
					String message = "";
					for (ConstraintViolation<?> constraintValidation : constraintViolationException
							.getConstraintViolations()) {
						message += "<br/><b>" + constraintValidation.getPropertyPath().toString() + "</b> "
								+ constraintValidation.getMessage();
					}
					return doEdit(type, idString, size, page, message, model);
				}
				throw transactionException;
			}

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Delete element by its id.
	 *
	 * @param type
	 *            the domain type
	 * @param id
	 *            element id
	 * @param page
	 *            current page
	 * @param size
	 *            current page size
	 * @param model
	 *            current model
	 * @return delete template name
	 */
	@RequestMapping(value = "${crudadmin.url}/delete/{type}/{size}/{page}/{id}/")
	public String delete(@PathVariable String type, @PathVariable String id, @PathVariable int size,
			@PathVariable int page, Model model) {
		CrudAdminRepository repoAdmin = repositoryMap.get(type);
		Serializable objectId = getIdFromString(id, repoAdmin);
		repoAdmin.getRepositoryObjectSerializable().delete(objectId);
		return "redirect:/" + crudAdminProperties.getUrl() + "/list/" + type + "/" + size + "/" + page;
	}

	private List<CrudAdminObjectField> getFields(CrudAdminRepository repoAdmin, String id) {
		List<CrudAdminObjectField> fields = new ArrayList<>();
		try {

			Object dao = null;
			// Id
			String idValue = "";
			if (id == null) {
				dao = repoAdmin.getDomainType().newInstance();
			} else {
				dao = repoAdmin.getRepositoryObjectSerializable()
						.findOne(getIdFromString(id, repoAdmin));
				idValue = defaultString(repoAdmin.getId().get(dao));
			}
			fields.add(new CrudAdminObjectField("id", idValue,repoAdmin.getId()));
			// Fields
			for (Field field : repoAdmin.getFields()) {
				Object valObject = field.get(dao);
				String value = valObject instanceof Date ? SDF.format(valObject) : defaultString(valObject);
				fields.add(new CrudAdminObjectField(field.getName(), value,field));
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return fields;
	}

	private List<CrudAdminObject> getObjects(CrudAdminRepository repoAdmin, Pageable pageable) {
		List<CrudAdminObject> repoObjects = new ArrayList<>();
		try {
			for (Object object : repoAdmin.getRepository().findAll(pageable)) {
				Object objectId = repoAdmin.getId().get(object);
				String id = objectId == null ? "" : idSerializer.toString(objectId, repoAdmin.getIdType());
				List<String> fields = new ArrayList<>();
				fields.add(defaultString(objectId));
				for (Field field : repoAdmin.getFields()) {
					Object valObject = field.get(object);
					String value = valObject instanceof Date ? CrudAdminUtils.SDF.format(valObject)
							: defaultString(valObject);
					fields.add(value);
				}
				CrudAdminObject repoObject = new CrudAdminObject(id, fields);
				repoObjects.add(repoObject);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return repoObjects;
	}

	/**
	 * Show edit page
	 * 
	 * @param type
	 *            the domain type
	 * @param id
	 *            element id
	 * @param page
	 *            current page
	 * @param size
	 *            current page size
	 * @param model
	 *            current model
	 * @param message
	 *            error message
	 * @return edit template name
	 */
	private String doEdit(@PathVariable String type, @PathVariable String id, @PathVariable int size,
			@PathVariable int page, String message, Model model) {
		CrudAdminRepository repoAdmin = repositoryMap.get(type);
		addAttributes(model, repoAdmin, page, size);
		model.addAttribute("fields", getFields(repoAdmin, id));
		model.addAttribute("message", message);
		model.addAttribute("idreadonly", id != null || repoAdmin.isGeneratedId());
		model.addAttribute("isnew", id == null);
		model.addAttribute("id",id);
		return crudAdminProperties.getTemplateedit();
	}

	/**
	 * Add all common attributes used in all templates
	 * 
	 * @param model
	 *            current model
	 * @param repoAdmin
	 *            current repository model
	 * @param page
	 *            current page
	 * @param size
	 *            current page size
	 */
	private void addAttributes(Model model, CrudAdminRepository repoAdmin, int page, int size) {
		model.addAttribute("adminpath", crudAdminProperties.getUrl());
		model.addAttribute("domainname", repoAdmin.getDomainTypeName());
		model.addAttribute("domainnamelowercase", repoAdmin.getDomainTypeNameLowerCase());
		model.addAttribute("formatteddomainname", CrudAdminUtils.formatField(repoAdmin.getDomainTypeName()));
		model.addAttribute("cdnlibraries",crudAdminProperties.getCdnLibraries());
		model.addAttribute("page", page);
		model.addAttribute("size", size);
		List<String> allDomainNames = repositoryMap.values().stream().map(repo -> repo.getDomainTypeName())
				.collect(Collectors.toList());
		model.addAttribute("alldomainnames", allDomainNames);
	}

	
	@SuppressWarnings("deprecation")
	private Serializable getIdFromString(String id, CrudAdminRepository repoAdmin){
		return idSerializer.fromString(URLDecoder.decode(id), repoAdmin.getIdType());
	}
	
	@SuppressWarnings("deprecation")
	private String getStringFromId(Object id, CrudAdminRepository repoAdmin){
		return URLEncoder.encode(idSerializer.toString(id, repoAdmin.getIdType()));
	}
	
}
