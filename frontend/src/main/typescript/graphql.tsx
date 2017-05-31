//  This file was automatically generated and should not be edited.
/* tslint:disable */

export interface NetworksQuery {
  // Fetches all versions
  networks: Array< {
    // The unique ID of this item
    id: string,
    // The unique name of this network
    name: string,
    // All of the servers belonging to this network
    servers: Array< {
      // The unique ID of this item
      id: string,
      // The unique name (per-network) of this server
      name: string,
      // The direct connect IP address of the server
      ip: string,
    } >,
  } >;
}
/* tslint:enable */
