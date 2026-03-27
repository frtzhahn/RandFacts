module com.randfacts{
	//to make JVM run these libraries with the project source code
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires transitive javafx.graphics;
	requires transitive javafx.base;

	//gives permission to the library to get in the source code directory com.mocha
	opens com.randfacts to javafx.fxml;

	//allows javafx library to see my source code for this project
	//so the library can communicate in the source code
	exports com.randfacts;
}
