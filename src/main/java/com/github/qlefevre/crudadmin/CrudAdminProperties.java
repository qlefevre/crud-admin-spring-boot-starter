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
package com.github.qlefevre.crudadmin;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties with prefix crudadmin
 */
@ConfigurationProperties("crudadmin")
public class CrudAdminProperties {
	
	/**
     * Whether to enable auto-configuration.
     */
    private boolean enabled = true;
    
    /**
     * Admin URL path
     */
	private String url = "admin";
    
    /**
     * Object Id Serializer
     */
    private String objectidserializer;
    
    /**
     * Default page size
     */
    private int defaultpagesize = 10;
    
    /**
     * Edit template name
     */
    private String templateedit = "adminedit";
    
    /**
     * List template name
     */
    private String templatelist = "adminlist";
    
    /**
     * View template name
     */
    private String templateview = "adminview";
    
    /**
     * CDN libraries prefix
     */
    private String cdnLibraries="";
    
	/**
	 * @return the cdnLibraries
	 */
	public String getCdnLibraries() {
		return cdnLibraries;
	}

	/**
	 * @param cdnLibraries the cdnLibraries to set
	 */
	public void setCdnLibraries(String cdnLibraries) {
		this.cdnLibraries = cdnLibraries;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the defaultpagesize
	 */
	public int getDefaultpagesize() {
		return defaultpagesize;
	}

	/**
	 * @param defaultpagesize the defaultpagesize to set
	 */
	public void setDefaultpagesize(int defaultpagesize) {
		this.defaultpagesize = defaultpagesize;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the templateedit
	 */
	public String getTemplateedit() {
		return templateedit;
	}

	/**
	 * @param templateedit the templateedit to set
	 */
	public void setTemplateedit(String templateedit) {
		this.templateedit = templateedit;
	}

	/**
	 * @return the templatelist
	 */
	public String getTemplatelist() {
		return templatelist;
	}

	/**
	 * @param templatelist the templatelist to set
	 */
	public void setTemplatelist(String templatelist) {
		this.templatelist = templatelist;
	}

	/**
	 * @return the templateview
	 */
	public String getTemplateview() {
		return templateview;
	}

	/**
	 * @param templateview the templateview to set
	 */
	public void setTemplateview(String templateview) {
		this.templateview = templateview;
	}

	/**
	 * @return the objectidserializer
	 */
	public String getObjectidserializer() {
		return objectidserializer;
	}

	/**
	 * @param objectidserializer the objectidserializer to set
	 */
	public void setObjectidserializer(String objectidserializer) {
		this.objectidserializer = objectidserializer;
	}
 
    
}
