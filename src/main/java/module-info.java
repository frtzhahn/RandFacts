module com.randfacts{
	//to make JVM run these libraries with the project source code UI section
	requires javafx.controls;
	requires javafx.fxml;

	// allows the use of http connection, dotenv, and gson
	requires java.net.http;
	requires io.github.cdimascio.dotenv.java;
	requires com.google.gson;

	// allows the projectto use jdbc
	requires java.sql;

	//gives access to these 
	opens com.randfacts to javafx.fxml, com.google.gson;

	//allows javafx library to see my source code for this project
	//so the library can communicate in the source code
	exports com.randfacts;
}
