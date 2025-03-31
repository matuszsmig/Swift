import psycopg2
import pandas as pd

DB_CONFIG = {
    "dbname": "Swift",
    "user": "postgres",
    "password": "db_password",
    "host": "db",
    "port": 5432
}

file_path = "db.xlsx"
df = pd.read_excel(file_path)

conn = psycopg2.connect(**DB_CONFIG)
cur = conn.cursor()

for _, row in df.iterrows():
    iso2_code = row["COUNTRY ISO2 CODE"]
    swift_code = row["SWIFT CODE"]
    code_type = row["CODE TYPE"]
    name = row["NAME"]
    address = row.get("ADDRESS", None)
    country_name = row["COUNTRY NAME"]
    time_zone = row.get("TIME ZONE")
    
    is_headquater = swift_code[8:] == "XXX"

    cur.execute("SELECT 1 FROM countries WHERE iso2_code = %s", (iso2_code,))
    if not cur.fetchone():
        cur.execute(
            "INSERT INTO countries (iso2_code, name, time_zone) VALUES (%s, %s, %s)",
            (iso2_code, country_name, time_zone)
        )

    if is_headquater:
        cur.execute("SELECT 1 FROM banks WHERE swift_code = %s", (swift_code,))
        if not cur.fetchone():
            cur.execute(
                "INSERT INTO banks (swift_code, name, address, country_iso2_code) VALUES (%s, %s, %s, %s)",
                (swift_code, name, address, iso2_code)
            )

for _, row in df.iterrows():
    iso2_code = row["COUNTRY ISO2 CODE"]
    swift_code = row["SWIFT CODE"]
    code_type = row["CODE TYPE"]
    name = row["NAME"]
    address = row.get("ADDRESS", None)
    country_name = row["COUNTRY NAME"]
    time_zone = row.get("TIME ZONE")
    
    is_headquater = swift_code[8:] == "XXX"

    if not is_headquater:
        headquarter_swift = swift_code[:8] + "XXX"
        
        cur.execute("SELECT 1 FROM banks WHERE swift_code = %s", (headquarter_swift,))
        if cur.fetchone():
            cur.execute("SELECT 1 FROM bank_branches WHERE swift_code = %s", (swift_code,))
            if not cur.fetchone():
                cur.execute(
                    "INSERT INTO bank_branches (swift_code, name, address, country_iso2_code, headquarters_swift_code) VALUES (%s, %s, %s, %s, %s)",
                    (swift_code, name, address, iso2_code, headquarter_swift)
                )    
        else:
            print(f"Didn't found headquarter for this bank branch: {swift_code}")

conn.commit()
cur.close()
conn.close()