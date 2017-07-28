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
package com.github.qlefevre.crudadmin.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.WebDataBinder;

import com.github.qlefevre.crudadmin.util.CrudAdminUtils;

/**
 * Object containing all useful information of the current
 * PagingAndSortingRepository
 * 
 * @author Quentin Lefèvre
 */
public class CrudAdminRepository {

	private Field idField;

	private PagingAndSortingRepository<?, ?> repository;

	private DefaultRepositoryMetadata defaultRepositoryMetadata;

	private WebDataBinder binder;

	public CrudAdminRepository(PagingAndSortingRepository<?, ?> repository,
			DefaultRepositoryMetadata defaultRepositoryMetadata) {
		this.repository = repository;
		this.defaultRepositoryMetadata = defaultRepositoryMetadata;
	}

	public PagingAndSortingRepository<?, ?> getRepository() {
		return repository;
	}

	@SuppressWarnings("unchecked")
	public PagingAndSortingRepository<Object, Serializable> getRepositoryObjectSerializable() {
		return ((PagingAndSortingRepository<Object, Serializable>) repository);
	}

	public void setRepository(PagingAndSortingRepository<?, ?> repository) {
		this.repository = repository;
	}

	public DefaultRepositoryMetadata getDefaultRepositoryMetadata() {
		return defaultRepositoryMetadata;
	}

	public void setDefaultRepositoryMetadata(DefaultRepositoryMetadata defaultRepositoryMetadata) {
		this.defaultRepositoryMetadata = defaultRepositoryMetadata;
	}

	public Class<?> getDomainType() {
		return defaultRepositoryMetadata.getDomainType();
	}

	public String getDomainTypeName() {
		return defaultRepositoryMetadata.getDomainType().getSimpleName();
	}

	public String getDomainTypeNameLowerCase() {
		return getDomainTypeName().toLowerCase();
	}

	public List<String> getTableHeaders() {
		List<String> fieldNames = new ArrayList<>();
		fieldNames.add(CrudAdminUtils.formatField(getId().getName()));
		fieldNames.addAll(getFields().stream().map(field -> CrudAdminUtils.formatField(field.getName()))
				.collect(Collectors.toList()));
		return fieldNames;
	}

	public List<Field> getFields() {
		List<Field> fieldNames = new ArrayList<>();
		Class<?> c = getDomainType();
		while (c != null) {
			for (Field field : c.getDeclaredFields()) {
				if (field.isAnnotationPresent(Column.class)) {
					field.setAccessible(true);
					fieldNames.add(field);
				}
			}
			c = c.getSuperclass();
		}
		return fieldNames;
	}

	public Field getId() {
		if (idField == null) {
			List<Field> fieldNames = new ArrayList<>();
			Class<?> c = getDomainType();
			while (c != null) {
				for (Field field : c.getDeclaredFields()) {
					if (field.isAnnotationPresent(Id.class)) {
						field.setAccessible(true);
						fieldNames.add(field);
					}
				}
				c = c.getSuperclass();
			}
			idField = fieldNames.get(0);
		}
		return idField;
	}

	public Class<?> getIdType() {
		return getId().getType();
	}

	public boolean isGeneratedId() {
		return getId().isAnnotationPresent(GeneratedValue.class);
	}

	public WebDataBinder getWebDataBinder(ServletRequestParameterPropertyValues values) {
		if (binder == null) {
			binder = new WebDataBinder(BeanUtils.instantiate(getDomainType()));
			binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(CrudAdminUtils.SDF, true));
			binder.registerCustomEditor(java.sql.Date.class, new CustomDateEditor(CrudAdminUtils.SDF, true));
		}
		binder.bind(values);
		return binder;
	}

	@Override
	public String toString() {
		return "[" + defaultRepositoryMetadata.getDomainType().getName() + " : "
				+ defaultRepositoryMetadata.getRepositoryInterface().getName() + "]";
	}

}
