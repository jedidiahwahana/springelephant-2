# psql-bot-bluemix #

This repository demonstrates how to create a basic bot with LINE Messaging API, ElephantSQL (PostgreSQL), and bluemix.

### How do I get set up? ###

* Configure your bot channel ini LINE@ developer

* Bind ElephantSQL service to your APP

* Make *manifest.yml* file
	
	```yml
	applications:
	- path: <YOUR_WAR_FILE_PATH>
  	  memory: 512M
  	  instances: 1
  	  domain: mybluemix.net
  	  name: <YOUR_BLUEMIX_APP_NAME>
  	  host: <YOUR_BLUEMIX_APP_NAME>
  	  disk_quota: 1024M
	```
	
* Get Database URL from environment variable in Bluemix

	```java
	@Bean
    public DataSource getDataSource()
    {
        String sServices=System.getenv("VCAP_SERVICES");
        JSONObject jServices=new JSONObject(sServices);
        JSONArray aElephant=jServices.getJSONArray("elephantsql");
        JSONObject jElephant=aElephant.getJSONObject(0);
        JSONObject jCredentials=jElephant.getJSONObject("credentials");
        String dbUrl=jCredentials.getString("uri");
        
        DriverManagerDataSource ds=new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(dbUrl);
        
        return ds;
    }
	```

* Compile
 
    ```bash
    $ gradle clean build
    ```

* Push to bluemix
	
	`$ cf push <YOUR_BLUEMIX_APP_NAME>`
	
* See logs

	`$ cf logs <YOUR_BLUEMIX_APP_NAME>`

### How do I contribute? ###

* Add your name and e-mail address into CONTRIBUTORS.txt
