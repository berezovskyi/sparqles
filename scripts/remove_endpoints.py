#!/usr/bin/env python3

import argparse
import configparser
import sys
from datetime import datetime, timedelta

from pymongo import MongoClient


def get_db_connection(properties_file):
    config = configparser.ConfigParser()

    # Add a default section since the file does not have one
    with open(properties_file, 'r') as f:
        config_string = '[default]\n' + f.read()
    config.read_string(config_string)

    db_host = config.get('default', 'db.host')
    db_port = config.getint('default', 'db.port')
    db_name = config.get('default', 'db.name')

    client = MongoClient(db_host, db_port, serverSelectionTimeoutMS=10000, socketTimeoutMS=3600000)
    return client[db_name]

COLLECTIONS_TO_CLEAN = [
    "atasks", "ctasks", "dtasks", "ftasks", "ptasks",
    "atasks_agg", "ctasks_agg", "dtasks_agg", "ftasks_agg", "ptasks_agg",
    "epview"
]

def remove_never_online_endpoints(db, dry_run=False):
    endpoints = db.endpoints.find()
    for endpoint in endpoints:
        uri = endpoint['uri']
        available_results = db.atasks.count_documents({'endpointResult.endpoint.uri': uri, 'isAvailable': True})
        if available_results == 0:
            print(f"Would remove endpoint (never online): {uri}")
            if not dry_run:
                db.endpoints.delete_one({'uri': uri})
                for collection_name in COLLECTIONS_TO_CLEAN:
                    if "agg" in collection_name or "epview" in collection_name:
                        db[collection_name].delete_many({'endpoint.uri': uri})
                    else:
                        db[collection_name].delete_many({'endpointResult.endpoint.uri': uri})

def remove_offline_endpoints(db, months, dry_run=False):
    cutoff_date = datetime.now() - timedelta(days=months * 30)
    cutoff_timestamp = int(cutoff_date.timestamp() * 1000)

    endpoints = db.endpoints.find()
    for endpoint in endpoints:
        uri = endpoint['uri']
        recent_available_results = db.atasks.count_documents({
            'endpointResult.endpoint.uri': uri,
            'isAvailable': True,
            'endpointResult.start': {'$gte': cutoff_timestamp}
        })
        if recent_available_results == 0:
            print(f"Would remove endpoint (offline for {months} months): {uri}")
            if not dry_run:
                db.endpoints.delete_one({'uri': uri})
                for collection_name in COLLECTIONS_TO_CLEAN:
                    if "agg" in collection_name or "epview" in collection_name:
                        db[collection_name].delete_many({'endpoint.uri': uri})
                    else:
                        db[collection_name].delete_many({'endpointResult.endpoint.uri': uri})

def main():
    parser = argparse.ArgumentParser(description='Remove endpoints from the database.')
    parser.add_argument('--properties', '-p', required=True, help='Path to the MongoDB properties file.')
    parser.add_argument('--never-online', action='store_true', help='Remove endpoints that have never been online.')
    parser.add_argument('--months-offline', type=int, help='Remove endpoints that have been offline for more than X months.')
    parser.add_argument('--dry-run', '-n', action='store_true', help='Perform a dry run without deleting any data.')

    args = parser.parse_args()

    if not args.never_online and not args.months_offline:
        print("Error: at least one of --never-online or --months-offline must be specified.", file=sys.stderr)
        sys.exit(1)

    db = get_db_connection(args.properties)

    if args.never_online:
        remove_never_online_endpoints(db, args.dry_run)

    if args.months_offline:
        remove_offline_endpoints(db, args.months_offline, args.dry_run)

if __name__ == '__main__':
    main()
