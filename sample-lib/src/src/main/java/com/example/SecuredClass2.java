/*
 * Copyright 2015 Agapsys Tecnologia Ltda-ME.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.agapsys.security.Secured;

public class SecuredClass2  {
	// CLASS SCOPE =============================================================
	@Secured
	public static class InnerSecuredClass1 {}
	
	public static class InnerSecuredClass2 {
		@Secured
		public void InnerSecuredClass2Method() {}
	}
	
	
	public static class InnerUnsecuredClass1 {}
	
	public static class InnerUnsecuredClass2 {}
	// =========================================================================
	
	// INSTANCE SCOPE ==========================================================
	@Secured
	public void securedClass2Method() {}
	// =========================================================================
}
