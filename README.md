# Distributed Transactions

This is a project to demonstrate distributed transactions management in Spring Boot. 
It uses JPA to manage database insertions into a PostgreSQL database running on the same machine and uses Spring's
transaction management annotations along with a transaction manager configuration to ensure that when performing a 
transaction against a data source either all insertions fail or succeed, but there is never a state where one succeeds
and the other fails.

## Setup

To run this project, some setup is required. The steps are as follows (assumes running on MacOS):
* Install PostgreSQL on the local system by using the command `brew install postgresql`
* Start PostgreSQL instance by running `pg_ctl -D /usr/local/var/postgres start` or, if you want it to run as a service 
on the machine i.e. start up automatically when the machine starts, you could use `brew services start postgresql` instead.
* Create a database on the machine matching the username of your current user (in my case johnkartupelis) by running the command
`createdb johnkartupelis`.
* Create table in the newly created database by using the psql CLI tool using simply `psql -f postgres_tables_setup.sql` 
(assuming you are running the command from the root directory of this project).
- Secondly, install MySQL on the local system using the command `brew install mysql`, then start the MySQL server by 
running `mysql.server start`
- Create the MySQL database by opening mysql CLI program by running `mysql -uroot`. Then create a database called 'test'
by running `create database test` in the MySQL client. After the database is created, then quit the CLI program by typing
'quit' followed by enter. Then re-enter the MySQL CLI by running `mysql -uroot test` to connect to the newly created 'test'
database.
- Within the MySQL database's CLI, run `source mysql_tables_setup.sql` to create the necessary table within the database.

## Running and Testing

The project can be run via IntelliJ by simply clicking the play (>) button in the upper right hand corner. The output
should show that the server starts and listens on port 8080.

If everything works correctly, then any string longer than 10 characters which is an insert attempts to place in the tables
should result in the database failing to input either of the objects, as the constraint will fail for the VARCHAR field
in the table transactiontwo within MySQL and therefore the transaction should roll back for both transactionone in Postgres
and transactiontwo in MySQL.

To test the above, do as follows:
1. Run the service
2. Hit the service on the URL localhost:8080/api/transaction with a JSON body of `{"information": "Test"}`
3. Go to the command line and type `psql` and hit enter to start the psql CLI tool. When the prompt starts in psql,
normal PostgreSQL queries can be run. If you run the command `SELECT * FROM transactionone` followed by opening
MySQL's CLI client using `mysql -uroot test` and run `SELECT * FROM
transactiontwo` then both should show a single row in the database, with a value for the information column of test.
4. Repeat the above, but change the post body to `{"information": "This is some text longer than ten characters"}`. The 
call should fail with a 500 error (there is not yet an error mapping class, so it just throws resulting in server exception
 -- in a real service instance this should probably map to a 4xx error if the string being over ten characters is not
 allowed by the request).
5. Run the commands in psql and mysql on the command line again. There should be no extra entries inserted into either table, showing
that the transaction for both has been rolled back because of the failure on the second table's entry.

## The Code

The main part of setting up JTA in a Spring app is just the configuration setup. See MainConfiguration, TransactionOneConfiguration
and TransactionTwoConfiguration to see the setup. The two data sources have to be manually setup, and then wrapped with an 
Atomikos (in this case - other JTA implementations are available) data source, allowing the transactions to be controlled together
by Atomikos and both transactions to be rolled back together. The way the data sources is setup, means that the model and the repositories
have to fall into different packages in Java so they can be scanned separately by Spring.
