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
* Create tables in the newly created database by using the psql CLI tool using simply `psql -f postgres_tables_setup.sql` 
(assuming you are running the command from the root directory of this project).

## Running and Testing

The project can be run via intelliJ by simply clicking the play (>) button in the upper right hand corner. The output
should show that the server starts and listens on port 8080.

If everything works correctly, then any string longer than 10 characters which is an insert attempts to place in the tables
should result in the database failing to input either of the objects, as the constraint will fail for the VARCHAR field
in the table transactiontwo and therefore the transaction should roll back for both transactionone and transactiontwo.

To test the above, do as follows:
1. Run the service
2. Hit the service on the URL localhost:8080/api/transaction with a JSON body of `{"information": "Test"}`
3. Go to the command line and type `psql` and hit enter to start the psql CLI tool. When the prompt starts in psql,
normal PostgreSQL queries can be run. If you run the command `SELECT * FROM transactionone` followed by `SELECT * FROM
transactiontwo` then both should show a single row in the database, with a value for the information column of test.
4. Repeat the above, but change the post body to `{"information": "This is some text longer than ten characters"}`. The 
call should fail with a 500 error (there is not yet an error mapping class, so it just throws resulting in server exception
 -- in a real service instance this should probably map to a 4xx error if the string being over ten characters is not
 allowed by the request).
5. Run the commands in psql on the command line again. There should be no extra entries inserted into either table, showing
that the transaction for both has been rolled back because of the failure on the second table's entry.
