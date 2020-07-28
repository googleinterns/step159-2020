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

// import com.fasterxml.jackson.databind.ObjectMapper;
// import java.io.IOException;
// import java.net.URI;
// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
// import java.util.HashMap;

// public final class PostRequestMaker {
//   public HttpRequest makeRequest(String schoolName, String courseName, String termName, Int
// units, String profName) {
//     var values = new HashMap<String, String>() {{
//         put("class-name", schoolName);
//         put ("course-name", courseName);
//         put("term", termName);
//         put ("units", units);
//         put ("professor-name", profName);
//     }};

//     var objectMapper = new ObjectMapper();
//     String requestBody = objectMapper
//             .writeValueAsString(values);

//     HttpClient client = HttpClient.newHttpClient();
//     HttpRequest request = HttpRequest.newBuilder()
//
// .uri(URI.create("https://8080-4b5d82bb-74f0-46b0-a124-e91877782c7c.us-west1.cloudshell.dev/add-info"));
//             .POST(HttpRequest.BodyPublishers.ofString(requestBody));
//             .build();
//     return(request);
//   }
//   public HttpResponse makeRepsonse(){
//         HttpResponse<String> response =
// client.send(request,HttpResponse.BodyHandlers.ofString());
//         return response;
//   }
// }
