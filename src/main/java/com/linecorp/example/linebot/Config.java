
package com.linecorp.example.linebot;

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
//        String jdbcUrl = "jdbc:" + dbUrl;
        
        DriverManagerDataSource ds=new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(dbUrl);
        
        return ds;
    }
    
    @Bean(name="com.linecorp.channel_secret")
    public String getChannelSecret()
    {
        return mEnv.getProperty("com.linecorp.channel_secret");
    }
    
    @Bean(name="com.linecorp.channel_access_token")
    public String getChannelAccessToken()
    {
        return mEnv.getProperty("com.linecorp.channel_access_token");
    }
    
    @Bean
    public PersonDao getPersonDao()
    {
        return new PersonDaoImpl(getDataSource());
    }
};
