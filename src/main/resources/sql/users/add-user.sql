INSERT INTO users_table(first_name, last_name, username, email, birthday)
VALUES
(
    :#${exchangeProperty.first}, :#${exchangeProperty.last}, :#${exchangeProperty.username},
    :#${exchangeProperty.email}, :#${exchangeProperty.birthday}
);