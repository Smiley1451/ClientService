-- Enable UUID extension if not already enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Create Client Profiles Table
CREATE TABLE client_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(50),
    profile_picture_url TEXT,

    -- Demographics / Location
    skills TEXT[],
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,

    -- AI & Trust Metrics
    ai_generated_summary TEXT,
    average_rating DOUBLE PRECISION DEFAULT 0.0,
    total_reviews INTEGER DEFAULT 0,
    job_success_rate DOUBLE PRECISION DEFAULT 100.0,
    experience_level VARCHAR(50),
    recommended_wage_per_hour DOUBLE PRECISION,
    profile_strength_score INTEGER,
    top_review_keywords TEXT[],
    last_ai_update TIMESTAMP WITH TIME ZONE,

    -- System Fields
    profile_completion_percent INTEGER,
    recommendation_flag BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    source VARCHAR(100)
);

-- Index for fast lookup by external user_id
CREATE INDEX idx_client_profiles_user_id ON client_profiles(user_id);


-- 2. Create Review Ratings Table
CREATE TABLE review_ratings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    worker_id VARCHAR(255) NOT NULL,
    reviewer_id VARCHAR(255) NOT NULL,

    -- Rating Data
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    review_text TEXT,

    -- Structured Scores
    punctuality_score INTEGER,
    quality_score INTEGER,
    behaviour_score INTEGER,

    job_id VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Index for fetching reviews by worker (for monthly analysis)
CREATE INDEX idx_review_ratings_worker_id ON review_ratings(worker_id);
-- Index for date range queries (for monthly scheduler)
CREATE INDEX idx_review_ratings_created_at ON review_ratings(created_at);