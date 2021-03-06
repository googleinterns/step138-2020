/*
 * Copyright 2019 Origma Pty Ltd (ACN 629 381 184) and contributors
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
 * 
 * Written by Ben McLean <ben@origma.com.au>, November 2019
 */
package perspective.src.au.com.origma.perspectiveapi.v1alpha1;

import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.AnalyzeCommentRequest;
import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.AnalyzeCommentResponse;
import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.AttributeType;
import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.ContentType;
import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.Entry;
import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.SuggestScoreRequest;
import perspective.src.au.com.origma.perspectiveapi.v1alpha1.models.SuggestScoreResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

/**
 * The default implementation of the PerspectiveAPI client, made using the Unirest library
 * 
 * @author Ben McLean &lt;ben@origma.com.au&gt;
 */
public class UnirestPerspectiveAPI implements PerspectiveAPI {
	
	String apiKey;
	boolean doNotStore = true;

	/**
	 * Create a new instance of UnirestPerspectiveAPI
	 * @param apiKey Your Google API Key
	 */
	public UnirestPerspectiveAPI(String apiKey) {
		super();
		this.apiKey = apiKey;
	}

	@Override
	public AnalyzeCommentResponse analyze(AnalyzeCommentRequest request) {
		HttpResponse<AnalyzeCommentResponse> response = Unirest.post(ANALYZE_ENDPOINT)
			.queryString("key", apiKey)
			.body(request)
			.asObject(AnalyzeCommentResponse.class);
		
		if(!response.isSuccess()){
			return null;
		}
		
		return response.getBody();
	}

	@Override
	public AnalyzeCommentResponse analyze(String comment) {
		return analyze(new AnalyzeCommentRequest.Builder()
				.addRequestedAttribute(AttributeType.TOXICITY, null)
				.comment(new Entry.Builder()
						.type(ContentType.PLAIN_TEXT)
						.text(comment)
						.build())
				.doNotStore(doNotStore)
				.build());
	}
	
	@Override
	public SuggestScoreResponse suggestScore(SuggestScoreRequest request) {
		HttpResponse<SuggestScoreResponse> response = Unirest.post(SUGGEST_ENDPOINT)
				.queryString("key", apiKey)
				.body(request)
				.asObject(SuggestScoreResponse.class);
			
			if(!response.isSuccess()){
				return null;
			}
			
			return response.getBody();
	}

	/**
	 * If the simple analysis will default to do not store
	 * @return the simple analysis will default to do not store
	 */
	public boolean isDoNotStore() {
		return doNotStore;
	}

	/**
	 * The default do not store setting
	 * @param doNotStore The default do not store setting
	 */
	public void setDoNotStore(boolean doNotStore) {
		this.doNotStore = doNotStore;
	}

}
