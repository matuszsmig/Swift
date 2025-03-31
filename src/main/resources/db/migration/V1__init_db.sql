DROP TABLE IF EXISTS "Swift".bank_branches;
DROP TABLE IF EXISTS "Swift".banks;
DROP TABLE IF EXISTS "Swift".countries;

DROP TABLE IF EXISTS countries;
CREATE TABLE countries (
    iso2_code CHAR(2) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    time_zone VARCHAR(64) NOT NULL
);

DROP TABLE IF EXISTS banks;
CREATE TABLE banks (
    swift_code CHAR(11) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    address VARCHAR(128),
    is_headquarter BOOLEAN NOT NULL DEFAULT TRUE,
    country_iso2_code CHAR(2) NOT NULL REFERENCES countries (iso2_code)
);

DROP TABLE IF EXISTS bank_branches;
CREATE TABLE bank_branches (
    swift_code CHAR(11) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    address VARCHAR(128),
    is_headquarter BOOLEAN NOT NULL DEFAULT FALSE,
    country_iso2_code CHAR(2) NOT NULL REFERENCES countries (iso2_code),
    headquarters_swift_code CHAR(11) NOT NULL REFERENCES banks (swift_code)
);

CREATE INDEX IF NOT EXISTS idx_banks_country ON banks (country_iso2_code);
CREATE INDEX IF NOT EXISTS idx_branches_country ON bank_branches (country_iso2_code);
CREATE INDEX IF NOT EXISTS idx_branches_headquarters ON bank_branches (headquarters_swift_code);
