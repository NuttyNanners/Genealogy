To compile:
javac --module-path "C:/Users/capta/Downloads/New folder (2)/openjfx-21.0.6_windows-x64_bin-sdk/javafx-sdk-21.0.6/lib" `      --add-modules javafx.controls,javafx.fxml `      -cp "lib/mysql-connector-j-8.0.33.jar" `      -d out src/main/java/edu/gmu/cs321/*.java

To run login screen:
java `
>>   --module-path "C:/Users/capta/Downloads/New folder (2)/openjfx-21.0.6_windows-x64_bin-sdk/javafx-sdk-21.0.6/lib" `
>>   --add-modules=javafx.controls,javafx.fxml `
>>   -cp "out;lib\mysql-connector-j-8.0.33.jar" `
>>   edu.gmu.cs321.LoginApp

You MUST run these commands within this directory. File paths may vary depending on your machine,
so make sure to modify them before running them!
