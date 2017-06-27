import {ComponentDecoratorInfer, connect, Dispatch, DispatchProp} from "react-redux";
import {AppStore} from "./AppStore";

export function connect_SD<TState, TDispatch>(
    map_S: (state: AppStore.All) => TState,
    map_D: (dispatch: Dispatch<any>) => TDispatch
): ComponentDecoratorInfer<DispatchProp<any>> {
    return connect(map_S, map_D);
}