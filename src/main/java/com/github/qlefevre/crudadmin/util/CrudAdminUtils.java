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
package com.github.qlefevre.crudadmin.util;

import java.text.SimpleDateFormat;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;

import com.github.qlefevre.crudadmin.model.CrudAdminRepository;

/**
 * Utility methods
 * 
 * @author Quentin Lefèvre
 */
public class CrudAdminUtils {
	
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String formatField(String field){
		char[] chars = field.toCharArray();
		String formatField = ""+Character.toUpperCase(chars[0]);
		for(int i = 1 ; i < chars.length ; i++){
			if(Character.isUpperCase(chars[i])){
				formatField += " "+Character.toLowerCase(chars[i]);
			}else{
				formatField += chars[i];
			}
		}
		return formatField;
	}
	
	public static CrudAdminRepository getDomainClass(PagingAndSortingRepository<?, ?> repo) {
		try {
			Class<?>[] classes = repo.getClass().getInterfaces();
			Class<?> repoClass = classes[0];
			DefaultRepositoryMetadata metadata = new DefaultRepositoryMetadata(repoClass);
			CrudAdminRepository repoAdmin = new CrudAdminRepository(repo, metadata);
			return repoAdmin;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	

	public static String defaultString(Object object) {
		return object != null ? object.toString() : "";
	}
}
