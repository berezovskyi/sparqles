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
    "epview", "robots", "schedule"
    # Note: "endpoints" collection is handled separately in the deletion logic
]

def get_collection_stats(db, collection_name, uri):
    """Get statistics about records to be deleted for an endpoint in a collection."""
    # Check if collection exists first
    if collection_name not in db.list_collection_names():
        return {'count': 0, 'oldest': None, 'newest': None}
        
    if "agg" in collection_name or collection_name in ["epview", "robots", "schedule"]:
        query = {'endpoint.uri': uri}
    else:
        query = {'endpointResult.endpoint.uri': uri}
    
    count = db[collection_name].count_documents(query)
    if count == 0:
        return {'count': 0, 'oldest': None, 'newest': None}
    
    # Get oldest and newest records based on timestamp
    if collection_name in ["epview", "robots", "schedule"]:
        # These collections don't have consistent timestamps or have different formats
        if collection_name == "robots":
            # robots collection has a timestamp field
            pipeline = [
                {'$match': query},
                {'$group': {
                    '_id': None,
                    'oldest': {'$min': '$timestamp'},
                    'newest': {'$max': '$timestamp'}
                }}
            ]
        else:
            # epview and schedule don't have meaningful timestamps for our purposes
            return {'count': count, 'oldest': 'N/A', 'newest': 'N/A'}
    elif "agg" in collection_name:
        # agg collections use lastUpdate
        pipeline = [
            {'$match': query},
            {'$group': {
                '_id': None,
                'oldest': {'$min': '$lastUpdate'},
                'newest': {'$max': '$lastUpdate'}
            }}
        ]
    else:
        # Regular task collections use endpointResult.start
        pipeline = [
            {'$match': query},
            {'$group': {
                '_id': None,
                'oldest': {'$min': '$endpointResult.start'},
                'newest': {'$max': '$endpointResult.start'}
            }}
        ]
    
    result = list(db[collection_name].aggregate(pipeline))
    if result and result[0]['oldest'] is not None:
        # Handle different timestamp formats
        oldest_ts = result[0]['oldest']
        newest_ts = result[0]['newest']
        
        if collection_name == "robots" and oldest_ts == 0:
            # robots collection may have 0 timestamps
            return {'count': count, 'oldest': 'N/A', 'newest': 'N/A'}
        
        # Convert millisecond timestamps to readable format
        if oldest_ts > 0:
            oldest_readable = datetime.fromtimestamp(oldest_ts / 1000).isoformat()
        else:
            oldest_readable = 'N/A'
            
        if newest_ts > 0:
            newest_readable = datetime.fromtimestamp(newest_ts / 1000).isoformat()
        else:
            newest_readable = 'N/A'
            
        return {
            'count': count,
            'oldest': oldest_readable,
            'newest': newest_readable
        }
    else:
        return {'count': count, 'oldest': 'N/A', 'newest': 'N/A'}

def remove_never_online_endpoints(db, dry_run=False):
    endpoints = db.endpoints.find()
    removed_count = 0
    total_endpoints = db.endpoints.count_documents({})
    
    print(f"Scanning {total_endpoints} endpoints for those that have never been online...")
    
    for endpoint in endpoints:
        uri = endpoint['uri']
        available_results = db.atasks.count_documents({'endpointResult.endpoint.uri': uri, 'isAvailable': True})
        
        if available_results == 0:
            print(f"\n{'[DRY RUN] ' if dry_run else ''}Endpoint never online: {uri}")
            
            if dry_run:
                # Show detailed information about what would be deleted
                print("  Data to be removed:")
                total_records = 0
                for collection_name in COLLECTIONS_TO_CLEAN:
                    stats = get_collection_stats(db, collection_name, uri)
                    if stats['count'] > 0:
                        print(f"    {collection_name}: {stats['count']} records "
                              f"(oldest: {stats['oldest']}, newest: {stats['newest']})")
                        total_records += stats['count']
                print(f"  Total records to be removed: {total_records}")
            else:
                # Actually delete the data
                db.endpoints.delete_one({'uri': uri})
                for collection_name in COLLECTIONS_TO_CLEAN:
                    if collection_name not in db.list_collection_names():
                        continue  # Skip non-existent collections
                    
                    if "agg" in collection_name or collection_name in ["epview", "robots", "schedule"]:
                        result = db[collection_name].delete_many({'endpoint.uri': uri})
                    else:
                        result = db[collection_name].delete_many({'endpointResult.endpoint.uri': uri})
                    if result.deleted_count > 0:
                        print(f"  Removed {result.deleted_count} records from {collection_name}")
                
            removed_count += 1
        else:
            # Show when this endpoint was last online (not being removed)
            last_available_record = db.atasks.find_one(
                {
                    'endpointResult.endpoint.uri': uri,
                    'isAvailable': True
                },
                sort=[('endpointResult.start', -1)]
            )
            
            if last_available_record:
                last_available_date = datetime.fromtimestamp(last_available_record['endpointResult']['start'] / 1000)
                print(f"✓ Keeping endpoint (last online: {last_available_date.isoformat()}): {uri}")
    
    # Safety warning for large deletions
    percentage = (removed_count / total_endpoints * 100) if total_endpoints > 0 else 0
    if percentage > 50 and not dry_run:
        print(f"\nWARNING: This operation would remove {percentage:.1f}% of all endpoints!")
    
    print(f"\n{'[DRY RUN] ' if dry_run else ''}Total endpoints {'would be' if dry_run else ''} removed (never online): {removed_count}")
    print(f"Total endpoints kept: {total_endpoints - removed_count}")

def get_collection_stats_from_date(db, collection_name, uri, from_timestamp):
    """Get statistics about records to be deleted for an endpoint in a collection from a specific date."""
    # Check if collection exists first
    if collection_name not in db.list_collection_names():
        return {'count': 0, 'oldest': None, 'newest': None}
        
    if "agg" in collection_name or collection_name in ["epview", "robots", "schedule"]:
        query = {'endpoint.uri': uri}
        # For these collections, we remove everything since they're aggregated/summary data
    else:
        query = {
            'endpointResult.endpoint.uri': uri,
            'endpointResult.start': {'$gte': from_timestamp}
        }
    
    count = db[collection_name].count_documents(query)
    if count == 0:
        return {'count': 0, 'oldest': None, 'newest': None}
    
    # Get oldest and newest records based on timestamp for the data to be deleted
    if collection_name in ["epview", "robots", "schedule"]:
        return {'count': count, 'oldest': 'N/A', 'newest': 'N/A'}
    elif "agg" in collection_name:
        # agg collections - remove all since they're aggregated data
        pipeline = [
            {'$match': {'endpoint.uri': uri}},
            {'$group': {
                '_id': None,
                'oldest': {'$min': '$lastUpdate'},
                'newest': {'$max': '$lastUpdate'}
            }}
        ]
    else:
        # Regular task collections - only data from the cutoff date
        pipeline = [
            {'$match': query},
            {'$group': {
                '_id': None,
                'oldest': {'$min': '$endpointResult.start'},
                'newest': {'$max': '$endpointResult.start'}
            }}
        ]
    
    result = list(db[collection_name].aggregate(pipeline))
    if result and result[0]['oldest'] is not None:
        oldest_ts = result[0]['oldest']
        newest_ts = result[0]['newest']
        
        # Convert millisecond timestamps to readable format
        if oldest_ts > 0:
            oldest_readable = datetime.fromtimestamp(oldest_ts / 1000).isoformat()
        else:
            oldest_readable = 'N/A'
            
        if newest_ts > 0:
            newest_readable = datetime.fromtimestamp(newest_ts / 1000).isoformat()
        else:
            newest_readable = 'N/A'
            
        return {
            'count': count,
            'oldest': oldest_readable,
            'newest': newest_readable
        }
    else:
        return {'count': count, 'oldest': 'N/A', 'newest': 'N/A'}

def remove_offline_endpoints(db, months, dry_run=False):
    cutoff_date = datetime.now() - timedelta(days=months * 30)
    cutoff_timestamp = int(cutoff_date.timestamp() * 1000)
    
    print(f"Checking for endpoints offline since: {cutoff_date.isoformat()}")
    
    endpoints = db.endpoints.find()
    removed_count = 0
    total_endpoints = db.endpoints.count_documents({})
    
    print(f"Scanning {total_endpoints} endpoints for those offline for more than {months} months...")
    
    for endpoint in endpoints:
        uri = endpoint['uri']
        
        # Check if endpoint has been available in any of the task collections recently
        recent_available = False
        recent_available_results = db.atasks.count_documents({
            'endpointResult.endpoint.uri': uri,
            'isAvailable': True,
            'endpointResult.start': {'$gte': cutoff_timestamp}
        })
        if recent_available_results > 0:
            recent_available = True
        
        if not recent_available:
            # Double-check that the endpoint has some historical data (not completely new)
            total_attempts = db.atasks.count_documents({'endpointResult.endpoint.uri': uri})
            if total_attempts == 0:
                continue  # Skip endpoints with no attempts at all
                
            # Find the last time this endpoint was available
            last_available_record = db.atasks.find_one(
                {
                    'endpointResult.endpoint.uri': uri,
                    'isAvailable': True
                },
                sort=[('endpointResult.start', -1)]
            )
            
            if last_available_record:
                last_available_timestamp = last_available_record['endpointResult']['start']
                last_available_date = datetime.fromtimestamp(last_available_timestamp / 1000)
                print(f"\n{'[DRY RUN] ' if dry_run else ''}Endpoint offline for {months} months: {uri}")
                print(f"  Last available: {last_available_date.isoformat()}")
                print(f"  Will preserve data before {last_available_date.isoformat()} and remove endpoint + data after that date")
            else:
                # Endpoint was never available - treat like never-online case
                last_available_timestamp = 0
                print(f"\n{'[DRY RUN] ' if dry_run else ''}Endpoint offline for {months} months (never available): {uri}")
                print(f"  Will remove endpoint and all associated data")
            
            print(f"  Total historical attempts: {total_attempts}")
            
            if dry_run:
                # Show detailed information about what would be deleted
                print("  Data to be removed:")
                total_records = 0
                for collection_name in COLLECTIONS_TO_CLEAN:
                    if collection_name == "endpoints":
                        continue  # We'll handle endpoints separately
                        
                    stats = get_collection_stats_from_date(db, collection_name, uri, last_available_timestamp)
                    if stats['count'] > 0:
                        print(f"    {collection_name}: {stats['count']} records "
                              f"(oldest: {stats['oldest']}, newest: {stats['newest']})")
                        total_records += stats['count']
                print(f"  Endpoint record: 1 record")
                print(f"  Total records to be removed: {total_records + 1}")
            else:
                # Actually delete the data
                print(f"  Removing endpoint record...")
                db.endpoints.delete_one({'uri': uri})
                
                for collection_name in COLLECTIONS_TO_CLEAN:
                    if collection_name == "endpoints":
                        continue  # Already handled above
                        
                    if collection_name not in db.list_collection_names():
                        continue  # Skip non-existent collections
                    
                    if "agg" in collection_name or collection_name in ["epview", "robots", "schedule"]:
                        # For aggregate collections, remove all data since it's summary data
                        result = db[collection_name].delete_many({'endpoint.uri': uri})
                    else:
                        # For task collections, only remove data from when endpoint became unavailable
                        result = db[collection_name].delete_many({
                            'endpointResult.endpoint.uri': uri,
                            'endpointResult.start': {'$gte': last_available_timestamp}
                        })
                    
                    if result.deleted_count > 0:
                        print(f"  Removed {result.deleted_count} records from {collection_name}")
                
            removed_count += 1
        else:
            # Show when this endpoint was last online (not being removed)
            last_available_record = db.atasks.find_one(
                {
                    'endpointResult.endpoint.uri': uri,
                    'isAvailable': True
                },
                sort=[('endpointResult.start', -1)]
            )
            
            if last_available_record:
                last_available_date = datetime.fromtimestamp(last_available_record['endpointResult']['start'] / 1000)
                print(f"✓ Keeping endpoint (last online: {last_available_date.isoformat()}): {uri}")
    
    # Safety warning for large deletions
    percentage = (removed_count / total_endpoints * 100) if total_endpoints > 0 else 0
    if percentage > 50 and not dry_run:
        print(f"\nWARNING: This operation would remove {percentage:.1f}% of all endpoints!")
    
    print(f"\n{'[DRY RUN] ' if dry_run else ''}Total endpoints {'would be' if dry_run else ''} removed (offline for {months} months): {removed_count}")
    print(f"Total endpoints kept: {total_endpoints - removed_count}")

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

    try:
        db = get_db_connection(args.properties)
        print(f"Connected to MongoDB database: {db.name}")
        
        if args.dry_run:
            print("\n*** DRY RUN MODE - No data will be deleted ***\n")
        
        total_endpoints_before = db.endpoints.count_documents({})
        print(f"Total endpoints in database: {total_endpoints_before}")

        if args.never_online:
            print(f"\n=== Checking for endpoints that have never been online ===")
            remove_never_online_endpoints(db, args.dry_run)

        if args.months_offline:
            print(f"\n=== Checking for endpoints offline for more than {args.months_offline} months ===")
            remove_offline_endpoints(db, args.months_offline, args.dry_run)
        
        if not args.dry_run:
            total_endpoints_after = db.endpoints.count_documents({})
            print(f"\nTotal endpoints remaining: {total_endpoints_after}")
            print(f"Endpoints removed: {total_endpoints_before - total_endpoints_after}")
            
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == '__main__':
    main()
