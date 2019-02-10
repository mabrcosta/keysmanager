# KeysManager
Simple ssh access manager through authorized keys

It supports the following features:

* Users and users groups
* Machines and machines groups
* Access defined through User <-> Machine, Users Group <-> Machine,  User <-> Machines Groups, Users Group <-> Machines Groups
* Date time range limited access

Currently the access configuration  is only available directly through the database.

#### Running

The application uses Typesafe Config and there is an example configuration file provided at `conf/application.conf.example`. 
To supply it to the application it simply rename it to `application.conf` 
or provide its path through the environment variable `-Dconfig.file=path/to/config-file`.

By default a Postgres database instance is used. The default database name is 'keysmanager'.
This can be modified by updating the configuration file.

Before starting the application create the database, the persistence schema is automatically applied at startup.


Having both set up, you just have to:


        $ sbt run
        
        
There is a access configuration mock data available at `etc/mock_data.sql`
        
#### Setting a the machine authentication
There is a provided authorized keys command script template available at `etc/authorizedKeysCommand.sh` 
        
To authenticate with the application authorized keys modify this template accordingly and copy it to the intended machine.
A suggested location would be at `/usr/local/bin/authorizedKeysCommand.sh`.

Give it execution permissions with `sudo chmod a+x /usr/local/bin/authorizedKeysCommand.sh`

Then in your machine `/etc/sshd_config` you need only add:

    AuthorizedKeysCommand /usr/local/bin/authorizedKeysCommand.sh
    AuthorizedKeysCommandUser nobody

#### Testing

To run all tests:


        $ sbt test

#### API Usage

Getting a hostname authorized_keys:
```
curl http://localhost:9000/machines/10.10.10.1/authorized_keys
```

