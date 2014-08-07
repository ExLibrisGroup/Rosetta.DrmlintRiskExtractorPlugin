/*
 * Copyright 2013 The British Library/SCAPE Project Consortium
 * Author: William Palmer (William.Palmer@bl.uk)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.exlibris.dps.repository.plugin.riskExtractor.drmlint;

/**
 * Enum for more fine-grained detection notification
 * @author wpalmer
 *
 */
public enum DRMFound {

	/**
	 * Unknown/undefined whether DRM exists
	 */
	UNKNOWN,
	/**
	 * DRM was found
	 */
	FOUND,
	/**
	 * No DRM was found
	 */
	NOT_FOUND;

}
