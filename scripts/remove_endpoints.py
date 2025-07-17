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

    client = MongoClient(db_host, db_port)
    return client[db_name]

def remove_never_online_endpoints(db, dry_run=False):
    endpoints = db.endpoints.find()
    for endpoint in endpoints:
        uri = endpoint['uri']
        available_results = db.aresults.count_documents({'endpointResult.endpoint.uri': uri, 'isAvailable': True})
        if available_results == 0:
            aresults_to_delete = db.aresults.find({'endpointResult.endpoint.uri': uri})
            count = db.aresults.count_documents({'endpointResult.endpoint.uri': uri})
            if count > 0:
                oldest = aresults_to_delete.sort('endpointResult.start', 1).limit(1)[0]['endpointResult']['start']
                newest = aresults_to_delete.sort('endpointResult.start', -1).limit(1)[0]['endpointResult']['start']
                print(f"Would remove endpoint (never online): {uri} and {count} associated aresults (oldest: {oldest}, newest: {newest})")
            else:
                print(f"Would remove endpoint (never online): {uri} and 0 associated aresults")

            if not dry_run:
                db.endpoints.delete_one({'uri': uri})
                db.aresults.delete_many({'endpointResult.endpoint.uri': uri})

def remove_offline_endpoints(db, months, dry_run=False):
    cutoff_date = datetime.now() - timedelta(days=months * 30)
    cutoff_timestamp = int(cutoff_date.timestamp() * 1000)

    endpoints = db.endpoints.find()
    for endpoint in endpoints:
        uri = endpoint['uri']
        recent_available_results = db.aresults.count_documents({
            'endpointResult.endpoint.uri': uri,
            'isAvailable': True,
            'endpointResult.start': {'$gte': cutoff_timestamp}
        })
        if recent_available_results == 0:
            # Find the last time the endpoint was online
            last_online_result = db.aresults.find({'endpointResult.endpoint.uri': uri, 'isAvailable': True}).sort('endpointResult.start', -1).limit(1)
            if last_online_result.count() > 0:
                last_online_timestamp = last_online_result[0]['endpointResult']['start']
                aresults_to_delete = db.aresults.find({'endpointResult.endpoint.uri': uri, 'endpointResult.start': {'$gte': last_online_timestamp}})
                count = db.aresults.count_documents({'endpointResult.endpoint.uri': uri, 'endpointResult.start': {'$gte': last_online_timestamp}})
                if count > 0:
                    oldest = aresults_to_delete.sort('endpointResult.start', 1).limit(1)[0]['endpointResult']['start']
                    newest = aresults_to_delete.sort('endpointResult.start', -1).limit(1)[0]['endpointResult']['start']
                    print(f"Would remove endpoint (offline for {months} months): {uri} and {count} associated aresults since last online (oldest: {oldest}, newest: {newest})")
                else:
                    print(f"Would remove endpoint (offline for {months} months): {uri} and 0 associated aresults")

                if not dry_run:
                    db.endpoints.delete_one({'uri': uri})
                    db.aresults.delete_many({'endpointResult.endpoint.uri': uri, 'endpointResult.start': {'$gte': last_online_timestamp}})

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
