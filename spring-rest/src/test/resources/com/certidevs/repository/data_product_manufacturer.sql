

INSERT INTO manufacturer (city, country, name, start_year)
VALUES ('Madrid', 'Spain', 'Adidas', 1990),
       ('Gijon', 'Spain', 'Nike', 1990);

INSERT INTO product (name, price, quantity, active, manufacturer_id) VALUES
                                                        ('prod1', 20, 1, 0, 1),
                                                        ('prod2', 32, 1, 1,1),
                                                        ('prod3', 40, 1, 0, 1),
                                                        ('prod4', 50, 1, 1, 1),
                                                        ('prod5', 60, 1, 0, 2),
                                                        ('prod6', 70, 1, 0, 2),
                                                        ('prod7', 79, 1, 1, 2),
                                                        ('prod8', 90, 1, 0, 2);