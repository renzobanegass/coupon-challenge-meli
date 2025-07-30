CREATE TABLE items (
    id UUID PRIMARY KEY
);

CREATE TABLE coupon_result (
    id UUID PRIMARY KEY,
    total NUMERIC(10, 2) NOT NULL
);

CREATE TABLE coupon_result_items (
    coupon_result_id UUID NOT NULL,
    item_id UUID NOT NULL,
    PRIMARY KEY (coupon_result_id, item_id),
    FOREIGN KEY (coupon_result_id) REFERENCES coupon_result(id),
    FOREIGN KEY (item_id) REFERENCES items(id)
);
