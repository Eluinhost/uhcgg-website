# Clear

```postgresql
truncate table networks cascade; 
truncate table bans cascade; 
truncate table users cascade; 
truncate table user_roles cascade; 
truncate table servers cascade; 
truncate table scenarios cascade;
truncate table matches cascade;
truncate table match_scenarios cascade;
``` 

Run this SQL snippet to clear all the data from the tables used

# Generate

```sbtshell
project seed
re-start
```

Creates a file in called 'seed.sql' with the randomly generated seed data. Run the SQL file to insert the data

