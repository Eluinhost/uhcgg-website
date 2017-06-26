/* tslint:disable */
//  This file was automatically generated and should not be edited.

export type NetworkListQueryVariables = {
  first: number | null,
  after: string | null,
};

export type NetworkListQuery = {
  // List of all networks
  networks:  {
    __typename: string,
    // Information to aid in pagination.
    pageInfo:  {
      __typename: string,
      // When paginating forwards, the cursor to continue.
      endCursor: string | null,
      // When paginating forwards, are there more items?
      hasNextPage: boolean,
    },
    // A list of edges.
    edges:  Array< {
      __typename: string,
      // The item at the end of the edge.
      node:  {
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
        owner:  {
          __typename: string,
          // The ID of an object
          id: string,
          // The raw unique ID of this item
          rawId: string,
          // The unique username of this user
          username: string,
        },
      },
    } > | null,
  },
};

export type NetworkViewQueryVariables = {
  id: string,
};

export type NetworkViewQuery = {
  // Fetches an object given its ID
  node: ( {
      __typename: "Ban",
      // The id of the object.
      id: string,
    } | {
      __typename: "User",
      // The id of the object.
      id: string,
    } | {
      __typename: "Role",
      // The id of the object.
      id: string,
    } | {
      __typename: "Network",
      // The ID of an object
      id: string,
      // The raw unique ID of this item
      rawId: string,
      // When this item was first created
      created: string,
      // Whether this item has been deleted or not
      deleted: boolean,
      // When this item was last edited
      modified: string,
      // The unique name of this network
      name: string,
      // The unique tag of this network
      tag: string,
      // A markdown formatted description of this network
      description: string,
      // The owner of the network, has full control
      owner:  {
        __typename: string,
        // The ID of an object
        id: string,
        // The raw unique ID of this item
        rawId: string,
        // The unique username of this user
        username: string,
      },
    } | {
      __typename: "Server",
      // The id of the object.
      id: string,
    } | {
      __typename: "Region",
      // The id of the object.
      id: string,
    } | {
      __typename: "Match",
      // The id of the object.
      id: string,
    } | {
      __typename: "Version",
      // The id of the object.
      id: string,
    } | {
      __typename: "Style",
      // The id of the object.
      id: string,
    } | {
      __typename: "Scenario",
      // The id of the object.
      id: string,
    }
  ) | null,
};

export type ServerListQueryVariables = {
  networkId: string,
  after: string | null,
  first: number | null,
};

export type ServerListQuery = {
  // Fetches an object given its ID
  node: ( {
      __typename: "Ban",
      // The id of the object.
      id: string,
    } | {
      __typename: "User",
      // The id of the object.
      id: string,
    } | {
      __typename: "Role",
      // The id of the object.
      id: string,
    } | {
      __typename: "Network",
      // The ID of an object
      id: string,
      // A list of all servers belonging to this network
      servers:  {
        __typename: string,
        // Information to aid in pagination.
        pageInfo:  {
          __typename: string,
          // When paginating forwards, the cursor to continue.
          endCursor: string | null,
          // When paginating forwards, are there more items?
          hasNextPage: boolean,
        },
        // A list of edges.
        edges:  Array< {
          __typename: string,
          // The item at the end of the edge.
          node:  {
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
            owner:  {
              __typename: string,
              // The ID of an object
              id: string,
              // The raw unique ID of this item
              rawId: string,
              // The unique username of this user
              username: string,
            },
            // The region the server is hosted in
            region:  {
              __typename: string,
              // The ID of an object
              id: string,
              // The raw unique ID of this item
              rawId: string,
              // The 'short' verison of the name
              short: string,
            },
          },
        } > | null,
      },
    } | {
      __typename: "Server",
      // The id of the object.
      id: string,
    } | {
      __typename: "Region",
      // The id of the object.
      id: string,
    } | {
      __typename: "Match",
      // The id of the object.
      id: string,
    } | {
      __typename: "Version",
      // The id of the object.
      id: string,
    } | {
      __typename: "Style",
      // The id of the object.
      id: string,
    } | {
      __typename: "Scenario",
      // The id of the object.
      id: string,
    }
  ) | null,
};

export type RegisterMutationVariables = {
  email: string,
  password: string,
  token: string,
};

export type RegisterMutation = {
  register:  {
    __typename: string,
    // The ID of an object
    id: string,
    // The unique username of this user
    username: string,
  },
};

export type NetworkListOwnerFragment = {
  __typename: string,
  // The ID of an object
  id: string,
  // The raw unique ID of this item
  rawId: string,
  // The unique username of this user
  username: string,
};

export type NetworkListNetworkFragment = {
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
  owner:  {
    __typename: string,
    // The ID of an object
    id: string,
    // The raw unique ID of this item
    rawId: string,
    // The unique username of this user
    username: string,
  },
};

export type NetworkListNetworkConnectionFragment = {
  __typename: string,
  // Information to aid in pagination.
  pageInfo:  {
    __typename: string,
    // When paginating forwards, the cursor to continue.
    endCursor: string | null,
    // When paginating forwards, are there more items?
    hasNextPage: boolean,
  },
  // A list of edges.
  edges:  Array< {
    __typename: string,
    // The item at the end of the edge.
    node:  {
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
      owner:  {
        __typename: string,
        // The ID of an object
        id: string,
        // The raw unique ID of this item
        rawId: string,
        // The unique username of this user
        username: string,
      },
    },
  } > | null,
};

export type NetworkViewNetworkFragment = {
  __typename: string,
  // The raw unique ID of this item
  rawId: string,
  // When this item was first created
  created: string,
  // Whether this item has been deleted or not
  deleted: boolean,
  // When this item was last edited
  modified: string,
  // The unique name of this network
  name: string,
  // The unique tag of this network
  tag: string,
  // A markdown formatted description of this network
  description: string,
  // The owner of the network, has full control
  owner:  {
    __typename: string,
    // The ID of an object
    id: string,
    // The raw unique ID of this item
    rawId: string,
    // The unique username of this user
    username: string,
  },
};

export type ServerListRegionFragment = {
  __typename: string,
  // The ID of an object
  id: string,
  // The raw unique ID of this item
  rawId: string,
  // The 'short' verison of the name
  short: string,
};

export type ServerListOwnerFragment = {
  __typename: string,
  // The ID of an object
  id: string,
  // The raw unique ID of this item
  rawId: string,
  // The unique username of this user
  username: string,
};

export type ServerListServerFragment = {
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
  owner:  {
    __typename: string,
    // The ID of an object
    id: string,
    // The raw unique ID of this item
    rawId: string,
    // The unique username of this user
    username: string,
  },
  // The region the server is hosted in
  region:  {
    __typename: string,
    // The ID of an object
    id: string,
    // The raw unique ID of this item
    rawId: string,
    // The 'short' verison of the name
    short: string,
  },
};

export type ServerListNetworkFragment = {
  __typename: string,
  // A list of all servers belonging to this network
  servers:  {
    __typename: string,
    // Information to aid in pagination.
    pageInfo:  {
      __typename: string,
      // When paginating forwards, the cursor to continue.
      endCursor: string | null,
      // When paginating forwards, are there more items?
      hasNextPage: boolean,
    },
    // A list of edges.
    edges:  Array< {
      __typename: string,
      // The item at the end of the edge.
      node:  {
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
        owner:  {
          __typename: string,
          // The ID of an object
          id: string,
          // The raw unique ID of this item
          rawId: string,
          // The unique username of this user
          username: string,
        },
        // The region the server is hosted in
        region:  {
          __typename: string,
          // The ID of an object
          id: string,
          // The raw unique ID of this item
          rawId: string,
          // The 'short' verison of the name
          short: string,
        },
      },
    } > | null,
  },
};
/* tslint:enable */
