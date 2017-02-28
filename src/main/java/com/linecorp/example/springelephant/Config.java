
package com.linecorp.example.springelephant;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.core.env.Environment;

import org.json.JSONObject;
import org.json.JSONArray;

@Configuration
public class Config
{
    @Autowired
    Environment mEnv;
    
	@Bean
    public DataSource getDataSource()
    {
        String sServices=System.getenv("VCAP_SERVICES");
        JSONObject jServices=new JSONObject(sServices);
        JSONArray aElephant=jServices.getJSONArray("elephantsql");
        JSONObject jElephant=aElephant.getJSONObject(0);
        JSONObject jCredentials=jElephant.getJSONObject("credentials");
        String dbUrl=jCredentials.getString("uri");
        dbUrl = dbUrl.replaceAll("postgres", "postgresql");
        String jdbcUrl = "jdbc:" + dbUrl;
        System.out.println("Database URL: " + jdbcUrl);
        
        DriverManagerDataSource ds=new DriverManagerDataSource();
        System.out.println("Datasource made");
        ds.setDriverClassName("org.postgresql.Driver");
        System.out.println("Driver Class Name set");
        ds.setUrl(jdbcUrl);
        System.out.println("URL set");
        
        return ds;
    }
    
    @Bean
    public PersonDao getPersonDao()
    {
        return new PersonDaoImpl(getDataSource());
    }
};
