CREATE TABLE IF NOT EXISTS users (
  id VARCHAR(128) PRIMARY KEY,
  email VARCHAR(255),
  name VARCHAR(255),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS expenses (
  id VARCHAR(128) PRIMARY KEY,
  user_id VARCHAR(128) NOT NULL REFERENCES users(id),
  date DATE NOT NULL,
  description TEXT,
  amount INTEGER NOT NULL,
  category VARCHAR(100),
  payment_method VARCHAR(100),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
  year_month VARCHAR(7) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_user_year_month ON expenses(user_id, year_month);

CREATE TABLE IF NOT EXISTS revoked_tokens (
  id BIGSERIAL PRIMARY KEY,
  token TEXT NOT NULL,
  revoked_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_revoked_token ON revoked_tokens(token);
