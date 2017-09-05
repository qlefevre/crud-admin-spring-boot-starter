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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Range;

import com.github.qlefevre.crudadmin.util.CrudAdminUtils;

/**
 * POJO used in view and edit templates
 * 
 * @author Quentin Lefèvre
 */
public class CrudAdminObjectField {
	
	private String name;
	
	private String value;
	
	private String type;
	
	private long max = Long.MAX_VALUE;
	
	private long min = Long.MIN_VALUE;
	
	private int scale = 0;
	
	private List<String> enumValues;

	public CrudAdminObjectField(String name, String value, Field field) {
		super();
		this.name = name;
		this.value = value;
		checkInputType(field);
		checkNumberInputType(field);
	}

	private void checkInputType(Field field){
		type = "text";
		if(field.getAnnotation(Id.class) != null){
			type = "text";
		}else{
			Class<?> typeField = field.getType();
			if (boolean.class.equals(typeField) || Boolean.class.equals(typeField)) {
				type = "boolean";
			}else if (typeField.isAssignableFrom(Date.class)) {
				type = "date";
			}else if(typeField.isEnum()){
				type = "enum";
				setEnumValues(Arrays.stream(typeField.getEnumConstants()).map(Object::toString).collect(Collectors.toList()));
			}else if (int.class.equals(typeField) || Integer.class.equals(typeField)) {
				type = "number";
				min = Integer.MIN_VALUE;
				max = Integer.MAX_VALUE;
			} else if (long.class.equals(typeField) || Long.class.equals(typeField)) {
				type = "number";
				min = Long.MIN_VALUE;
				max = Long.MAX_VALUE;
			} else if (short.class.equals(typeField) || Short.class.equals(typeField)) {
				type = "number";
				min = Short.MIN_VALUE;
				max = Short.MAX_VALUE;
			} else if (byte.class.equals(typeField) || Byte.class.equals(typeField)) {
				type = "number";
				min = Byte.MIN_VALUE;
				max = Byte.MAX_VALUE;
			} else if (float.class.equals(typeField) || Float.class.equals(typeField) || double.class.equals(typeField)
					|| Double.class.equals(typeField)) {
				int scaleValue = CrudAdminUtils.getScale(field);
				if (scaleValue > 0) {
					scale = scaleValue;
					type = "number";
				}
			}
		}
	}
	
	private void checkNumberInputType(Field field) {
		if("number".equals(type)){
			Min min = field.getAnnotation(Min.class);
			if(min != null){
				this.min = min.value();
			}
			Max max = field.getAnnotation(Max.class);
			if(max != null){
				this.max = max.value();
			}
			Range range = field.getAnnotation(Range.class);
			if(range != null){
				this.min = range.min();
				this.max = range.max();
			}
		}
	}

	public String getFormattedName() {
		return CrudAdminUtils.formatField(getName());
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the max
	 */
	public long getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(long max) {
		this.max = max;
	}

	/**
	 * @return the min
	 */
	public long getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(long min) {
		this.min = min;
	}

	/**
	 * @return the scale
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(int scale) {
		this.scale = scale;
	}
	
	/**
	 * @return the step
	 */
	public double getStep(){
		return Math.pow(10, getScale()*-1);
	}

	public List<String> getEnumValues() {
		return enumValues;
	}

	public void setEnumValues(List<String> enumValues) {
		this.enumValues = enumValues;
	}
}
