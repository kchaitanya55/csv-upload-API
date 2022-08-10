# CSV UPLOAD API

## Overview
The API is responsible for Uploading CSV data of Products  to Database, Update the Product and Get the Product Information.

## Guidelines for Developer

1. Clone this Project
2. Please refer the products.csv file for testing the upload feature.
3.Run the Application as Java Application.

## Guidelines for User

####File Upload
1.Refer the below snippet while preparing CSV file for upload
```java
	prodID,prodName,ssid
        1,Fan,ABCDEF1234
        1,Cooler,ABCDEF1235
```
2.Hit the API "/csv-upload" with file property in body of the request.
4.Error/Success message of the body will be sent back as response.

####Product Update
1.To update the product hit the "/update" path with below Request body.
```java
	{
            "prodID":"1",
            "prodName":"ABCD",
            "ssid":"ABCD12346"
    }
```
2.Error/Success message of the body will be sent back as response.

####Get All Product Information

1.To get All the Product Information ,Hit the API with the path "/data".
2. You can pass the paramaters like number of pages,size and filtering and sorting is also available.
3. Please refer below snippets for reference.
```java
	"/data?data?page=0&size=10&sort=prodID,DESC&prodID=10"
```
```java
	"/data?data?page=2&size=10&sort=prodName"
```
```java
	"/data?data?page=2&size=10&createdBy=admin&sort=createdDate,ASC"
```

######Note:
Basic Authentication is required for accessing every path of API.Find the Below Credentials.
```java
	username=admin
        password=admin
```