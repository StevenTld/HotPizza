CREATE TABLE `users` (
  `id` integer PRIMARY KEY,
  `username` varchar(255) UNIQUE NOT NULL,
  `email` varchar(255) UNIQUE NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(255) DEFAULT 'client',
  `created_at` timestamp
);

CREATE TABLE `pizzas` (
  `id` integer PRIMARY KEY,
  `name` varchar(255) NOT NULL,
  `description` text,
  `photo` varchar(255),
  `created_at` timestamp
);

CREATE TABLE `ingredients` (
  `id` integer PRIMARY KEY,
  `name` varchar(255) UNIQUE NOT NULL,
  `description` text,
  `photo` varchar(255)
);

CREATE TABLE `pizza_ingredients` (
  `pizza_id` integer NOT NULL,
  `ingredient_id` integer NOT NULL,
  `is_optional` boolean DEFAULT false,
  `primary` key(pizza_id,ingredient_id)
);

CREATE TABLE `orders` (
  `id` integer PRIMARY KEY,
  `user_id` integer NOT NULL,
  `status` varchar(255) NOT NULL DEFAULT 'pending',
  `total_price` decimal(10,2) NOT NULL,
  `created_at` timestamp
);

CREATE TABLE `order_items` (
  `id` integer PRIMARY KEY,
  `order_id` integer NOT NULL,
  `pizza_id` integer NOT NULL,
  `quantity` integer NOT NULL DEFAULT 1,
  `price` decimal(10,2) NOT NULL
);

CREATE TABLE `order_item_ingredients` (
  `order_item_id` integer NOT NULL,
  `ingredient_id` integer NOT NULL,
  `primary` key(order_item_id,ingredient_id)
);

CREATE TABLE `admin_actions` (
  `id` integer PRIMARY KEY,
  `admin_id` integer NOT NULL,
  `action` text NOT NULL,
  `created_at` timestamp
);

CREATE TABLE `statistics` (
  `id` integer PRIMARY KEY,
  `total_orders` integer NOT NULL,
  `total_revenue` decimal(10,2) NOT NULL,
  `most_ordered_pizza_id` integer,
  `created_at` timestamp
);

ALTER TABLE `pizza_ingredients` ADD FOREIGN KEY (`pizza_id`) REFERENCES `pizzas` (`id`);

ALTER TABLE `pizza_ingredients` ADD FOREIGN KEY (`ingredient_id`) REFERENCES `ingredients` (`id`);

ALTER TABLE `orders` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `order_items` ADD FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);

ALTER TABLE `order_items` ADD FOREIGN KEY (`pizza_id`) REFERENCES `pizzas` (`id`);

ALTER TABLE `order_item_ingredients` ADD FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`id`);

ALTER TABLE `order_item_ingredients` ADD FOREIGN KEY (`ingredient_id`) REFERENCES `ingredients` (`id`);

ALTER TABLE `admin_actions` ADD FOREIGN KEY (`admin_id`) REFERENCES `users` (`id`);

ALTER TABLE `statistics` ADD FOREIGN KEY (`most_ordered_pizza_id`) REFERENCES `pizzas` (`id`);
