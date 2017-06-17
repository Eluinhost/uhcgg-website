/* tslint:disable */
//  This file was automatically generated and should not be edited.

export interface NetworksQuery {
  // Fetches all versions
  networks: Array< {
    __typename: string,
    // The ID of an object
    id: string,
    // The raw unique ID of this item
    rawId: string,
    // The unique name of this network
    name: string,
    // The unique tag of this network
    tag: string,
    // A markdown formatted description of this network
    description: string,
    // The owner of the network, has full control
    owner: {
      __typename: string,
      // The ID of an object
      id: string,
      // The raw unique ID of this item
      rawId: string,
      // The unique username of this user
      username: string,
    },
    // All of the servers belonging to this network
    servers: Array< {
      __typename: string,
      // The ID of an object
      id: string,
      // The raw unique ID of this item
      rawId: string,
      // The unique name (per-network) of this server
      name: string,
      // The direct connect IP address of the server
      ip: string,
      // Optional address to connect to the server
      address: string | null,
      // Text description of where about the server is hosted
      location: string,
      // Optional port to use to connect
      port: number | null,
      // The owner of this server, has control over this server
      owner: {
        __typename: string,
        // The ID of an object
        id: string,
        // The raw unique ID of this item
        rawId: string,
        // The unique username of this user
        username: string,
      },
      // The region the server is hosted in
      region: {
        __typename: string,
        // The ID of an object
        id: string,
        // The raw unique ID of this item
        rawId: string,
        // The 'short' verison of the name
        short: string,
      },
    } >,
  } >;
}

export interface RegisterMutationVariables {
  email: string;
  password: string;
  token: string;
}

export interface RegisterMutation {
  register: {
    __typename: string,
    // The ID of an object
    id: string,
    // The unique username of this user
    username: string,
  };
}
/* tslint:enable */
