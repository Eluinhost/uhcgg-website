/* tslint:disable */
//  This file was automatically generated and should not be edited.

export interface NetworkListQuery {
  // Fetches all versions
  networks: Array< NetworkListNetworkFragment & {
    __typename: string,
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

export interface NetworkListServerConnectionFragment {
  __typename: string;
  // Information to aid in pagination.
  pageInfo: {
    __typename: string,
    // When paginating backwards, the cursor to continue.
    startCursor: string | null,
    // When paginating forwards, the cursor to continue.
    endCursor: string | null,
  };
  // A list of edges.
  edges: Array< {
    __typename: string,
    // A cursor for use in pagination.
    cursor: string,
    // The item at the end of the edge.
    node: NetworkListServerFragment & {
      __typename: string,
    },
  } > | null;
}

export interface NetworkListServerFragment {
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
  owner: NetworkListOwnerFragment & {
    __typename: string,
  };
  // The region the server is hosted in
  region: NetworkListRegionFragment & {
    __typename: string,
  };
}

export interface NetworkListRegionFragment {
  __typename: string;
  // The ID of an object
  id: string;
  // The raw unique ID of this item
  rawId: string;
  // The 'short' verison of the name
  short: string;
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
  // A list of all servers belonging to this network
  servers: NetworkListServerConnectionFragment & {
    __typename: string,
  };
}
/* tslint:enable */
