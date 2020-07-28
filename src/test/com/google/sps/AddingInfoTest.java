// // // Copyright 2019 Google LLC
// // //
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

// import com.google.sps.AddSchoolInfo;
// import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
// import static org.junit.Assert.assertEquals;
// import com.google.appengine.api.datastore.DatastoreService;
// import com.google.appengine.api.datastore.DatastoreServiceFactory;
// import com.google.appengine.api.datastore.Entity;
// import com.google.appengine.api.datastore.Query;
// import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
// import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
// import com.google.sps.PostRequestMaker;
// import java.util.Arrays;
// import org.junit.Assert;
// import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.junit.runners.JUnit4;

// /** */
// @RunWith(JUnit4.class)
// public final class AddingInfoTest {
//   private AddingSchoolInfo newSchool;
//   private static final LocalServiceTestHelper helper =
//     new LocalServiceTestHelper(
//             new
// LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

//   @Before
//   public void setUp() {
//     helper.setUp();
//     SchoolInfo = new AddSchoolInfo();
//   }

//   @After
//   public void tearDown() {
//     helper.tearDown();
//   }

//   @Test
//   public void AddingNewSchoolShouldAddNewSchool() {
//     DatastoreService db = DatastoreServiceFactory.getDatastoreService();
//     PostRequestMaker modelRequest = PostRequestMaker.makeResponse("MIT","6.006","Spring
// 2020",12,"Jason Ku");
//     PostRequestMaker modelResponse = PostRequestMaker.makeRequest();
//     AddSchoolInfo.addSchoolData(db,modelRequest,modelResponse);

//     Filter schoolFilter = new FilterPredicate("school-name", FilterOperator.EQUAL,
// modelRequest.getParameter("class-name"));
//     Query schoolQ = new Query("school").setFilter(schoolFilter);
//     List<Entity> schoolQuery =
//         datastore.prepare(schoolQ).asList(FetchOptions.Builder.withDefaults());

//     Entity actual = schoolQuery.get(0);
//     Entity expected = new Entity("school");
//     expected.setProperty("school-name","MIT");
//     Assert.assertEquals(expected, actual);
//   }

//   @Test
//   public void AddingNewSchoolShouldAddNewCourse() {
//     DatastoreService db = DatastoreServiceFactory.getDatastoreService();
//     PostRequestMaker modelRequest = PostRequestMaker.makeResponse("MIT","6.006","Spring
// 2020",12,"Jason Ku");
//     PostRequestMaker modelResponse = PostRequestMaker.makeRequest();
//     AddSchoolInfo.addSchoolData(db,modelRequest,modelResponse);

//     Filter schoolFilter = new FilterPredicate("course-name", FilterOperator.EQUAL,
// modelRequest.getParameter("course-name"));
//     Query courseQ = new Query("course").setFilter(schoolFilter);
//     List<Entity> courseQuery =
//         datastore.prepare(courseQ).asList(FetchOptions.Builder.withDefaults());

//     Entity actual = courseQuery.get(0);
//     Entity expected = new Entity("course");
//     expected.setProperty("course-name","6.006");
//     Assert.assertEquals(expected, actual);
//   }

//   @Test
//   public void AddingNewSchoolShouldAddNewCourse() {
//     DatastoreService db = DatastoreServiceFactory.getDatastoreService();
//     PostRequestMaker modelRequest = PostRequestMaker.makeResponse("MIT","6.006","Spring
// 2020",12,"Jason Ku");
//     PostRequestMaker modelResponse = PostRequestMaker.makeRequest();
//     AddSchoolInfo.addSchoolData(db,modelRequest,modelResponse);

//     Filter schoolFilter = new FilterPredicate("course-name", FilterOperator.EQUAL,
// modelRequest.getParameter("course-name"));
//     Query courseQ = new Query("course").setFilter(schoolFilter);
//     List<Entity> courseQuery =
//         datastore.prepare(courseQ).asList(FetchOptions.Builder.withDefaults());

//     Entity actual = courseQuery.get(0);
//     Entity expected = new Entity("course");
//     expected.setProperty("course-name","6.006");
//     Assert.assertEquals(expected, actual);
//   }
// }
