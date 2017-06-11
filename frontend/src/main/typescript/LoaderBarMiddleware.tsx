import { MiddlewareInterface, MiddlewareRequest } from 'apollo-client/transport/middleware';
import { AfterwareInterface, AfterwareResponse } from 'apollo-client/transport/afterware';
import * as NProgress from 'nprogress';

export class LoaderBarMiddleware implements MiddlewareInterface, AfterwareInterface {
    inFlight: number = 0;

    constructor() {
        // fix `this` being rebound by apollo
        this.applyMiddleware = this.applyMiddleware.bind(this);
        this.applyAfterware = this.applyAfterware.bind(this);
    }

    applyMiddleware(request: MiddlewareRequest, next: Function): void {
        if (this.inFlight == 0) {
            NProgress.start();
        } else {
            NProgress.inc();
        }
        this.inFlight++;
        next();
    }

    applyAfterware(response: AfterwareResponse, next: Function): any {
        this.inFlight--;
        if (this.inFlight == 0) {
            NProgress.done();
        }
        next();
    }
}