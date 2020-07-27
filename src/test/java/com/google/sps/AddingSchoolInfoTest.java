// // Copyright 2019 Google LLC
// //
// // Licensed under the Apache License, Version 2.0 (the "License");
// // you may not use this file except in compliance with the License.
// // You may obtain a copy of the License at
// //
// //     https://www.apache.org/licenses/LICENSE-2.0
// //
// // Unless required by applicable law or agreed to in writing, software
// // distributed under the License is distributed on an "AS IS" BASIS,
// // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// // See the License for the specific language governing permissions and
// // limitations under the License.

// package com.google.sps;

// import com.google.sps.data.AddSchoolInfo;
// import com.google.sps.data.MockPostObject;
// import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
// import static org.junit.Assert.assertEquals;
// import com.google.appengine.api.datastore.DatastoreService;
// import com.google.appengine.api.datastore.DatastoreServiceFactory;
// import com.google.appengine.api.datastore.Entity;
// import com.google.appengine.api.datastore.Query;
// import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
// import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Collection;
// import java.util.Collections;
// import java.util.List;
// import org.junit.Assert;
// import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.junit.runners.JUnit4;

// /** */
// @RunWith(JUnit4.class)
// public final class AddingSchoolInfoTest {

//   private MockPostObject mockPost = new MockPostObject("MIT","6.006","Fall 2020",12,"Srini");

//   @Test
//   public void processPostRequestShouldPopulateObject() {

//     PostRequestObject testPostObject = AddSchoolInfo.proccesPostRequest(MockPostObject);
//     Assert.assertEquals(testPostObject.getSchool(), mockPost.getParameter("school-name"));
//     Assert.assertEquals(testPostObject.getCourse(), mockPost.getParameter("course-name"));
//     Assert.assertEquals(testPostObject.getTerm(), mockPost.getParameter("term"));
//     Assert.assertEquals(testPostObject.getUnits(), mockPost.getParameter("units"));
//     Assert.assertEquals(testPostObject.getProf(),mockPost.getParameter("professor-name"));
//   }
// }
