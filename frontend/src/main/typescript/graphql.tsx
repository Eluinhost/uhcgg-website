/* tslint:disable */
//  This file was automatically generated and should not be edited.

export interface NetworkListQueryVariables {
  first: number | null;
  after: string | null;
}

export interface NetworkListQuery {
  // List of all networks
  networks: NetworkListNetworkConnectionFragment & {
    __typename: string,
  };
}

export interface NetworkViewQueryVariables {
  id: string;
}

export interface NetworkViewQuery {
  // Fetches an object given its ID
  node: NetworkViewNetworkFragment & {
    __typename: string,
    // The id of the object.
    id: string,
  } | null;
}

export interface ServerListQueryVariables {
  networkId: string;
  after: string | null;
  first: number | null;
}

export interface ServerListQuery {
  // Fetches an object given its ID
  node: ServerListNetworkFragment & {
    __typename: string,
    // The id of the object.
    id: string,
  } | null;
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

export interface NetworkListOwnerFragment {
  __typename: string;
  // The ID of an object
  id: string;
  // The raw unique ID of this item
  rawId: string;
  // The unique username of this user
  username: string;
}

export interface NetworkListNetworkFragment {
  __typename: string;
  // The ID of an object
  id: string;
  // The raw unique ID of this item
  rawId: string;
  // The unique name of this network
  name: string;
  // The unique tag of this network
  tag: string;
  // A markdown formatted description of this network
  description: string;
  // The owner of the network, has full control
  owner: NetworkListOwnerFragment & {
    __typename: string,
  };
}

export interface NetworkListNetworkConnectionFragment {
  __typename: string;
  // Information to aid in pagination.
  pageInfo: {
    __typename: string,
    // When paginating forwards, the cursor to continue.
    endCursor: string | null,
    // When paginating forwards, are there more items?
    hasNextPage: boolean,
  };
  // A list of edges.
  edges: Array< {
    __typename: string,
    // The item at the end of the edge.
    node: NetworkListNetworkFragment & {
      __typename: string,
    },
  } > | null;
}

export interface NetworkViewNetworkFragment {
  __typename: string;
  // The raw unique ID of this item
  rawId: string;
  // When this item was first created
  created: string;
  // Whether this item has been deleted or not
  deleted: boolean;
  // When this item was last edited
  modified: string;
  // The unique name of this network
  name: string;
  // The unique tag of this network
  tag: string;
  // A markdown formatted description of this network
  description: string;
  // The owner of the network, has full control
  owner: {
    __typename: string,
    // The ID of an object
    id: string,
    // The raw unique ID of this item
    rawId: string,
    // The unique username of this user
    username: string,
  };
}

export interface ServerListRegionFragment {
  __typename: string;
  // The ID of an object
  id: string;
  // The raw unique ID of this item
  rawId: string;
  // The 'short' verison of the name
  short: string;
}

export interface ServerListOwnerFragment {
  __typename: string;
  // The ID of an object
  id: string;
  // The raw unique ID of this item
  rawId: string;
  // The unique username of this user
  username: string;
}

export interface ServerListServerFragment {
  __typename: string;
  // The ID of an object
  id: string;
  // The raw unique ID of this item
  rawId: string;
  // The unique name (per-network) of this server
  name: string;
  // The direct connect IP address of the server
  ip: string;
  // Optional address to connect to the server
  address: string | null;
  // Text description of where about the server is hosted
  location: string;
  // Optional port to use to connect
  port: number | null;
  // The owner of this server, has control over this server
  owner: ServerListOwnerFragment & {
    __typename: string,
  };
  // The region the server is hosted in
  region: ServerListRegionFragment & {
    __typename: string,
  };
}

export interface ServerListNetworkFragment {
  __typename: string;
  // A list of all servers belonging to this network
  servers: {
    __typename: string,
    // Information to aid in pagination.
    pageInfo: {
      __typename: string,
      // When paginating forwards, the cursor to continue.
      endCursor: string | null,
      // When paginating forwards, are there more items?
      hasNextPage: boolean,
    },
    // A list of edges.
    edges: Array< {
      __typename: string,
      // The item at the end of the edge.
      node: ServerListServerFragment & {
        __typename: string,
      },
    } > | null,
  };
}
/* tslint:enable */
