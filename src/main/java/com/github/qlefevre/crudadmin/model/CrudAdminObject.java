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

import java.net.URLEncoder;
import java.util.List;

/**
 * POJO used in list template
 * 
 * @author Quentin Lefèvre
 */
public class CrudAdminObject {
	
	private String id;
	
	private List<String> fields;

	public CrudAdminObject(String id, List<String> fields) {
		this.id = id;
		this.fields = fields;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public String getId() {
		return id;
	}
	
	@SuppressWarnings("deprecation")
	public String getUrlEncodedId(){
		return URLEncoder.encode(id);
	}
	

	public void setId(String id) {
		this.id = id;
	}

}
