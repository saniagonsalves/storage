#Storage

Steps to run:
1. Launch an EC2 instance and run sudo yum install -y java-1.8.0-openjdk-devel git maven 
2. Clone this repository
3. Enter required properties in application.properties
4. Run the application using sudo nohup java -jar com.saniagonsalves.storage.StorageApplication > run.log 2>&1 &
5. Monitor log using tail -f run.log