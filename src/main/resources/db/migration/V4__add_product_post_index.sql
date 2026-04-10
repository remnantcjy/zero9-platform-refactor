CREATE INDEX idx_product_status_updated
    ON product_posts(progress_status, updated_at DESC);