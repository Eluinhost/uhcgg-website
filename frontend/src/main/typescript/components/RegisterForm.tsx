import * as React from 'react';
import {DefaultChildProps, graphql } from 'react-apollo';
import { RegisterMutation, RegisterMutationVariables } from '../graphql';
import {FormErrors, FormProps, reduxForm, SubmissionError} from "redux-form";
import {InputField} from "./fields/InputField";
import {isApolloError} from "apollo-client/errors/ApolloError";

const registerMutation = require('../../graphql/register.graphql');

// Given in from the outside
export interface RegisterFormProps {
    username: string,
    token: string
}

// Data inside the form
export interface RegisterFormData {
    username: string,
    email: string,
    password: string,
    repeat: string
}

// props from graphql binding
export type RegisterFormGraphqlProps = DefaultChildProps<RegisterFormProps, RegisterMutation>
// props from reduxForm binding
export type RegisterFormFormProps = FormProps<RegisterFormData, RegisterFormProps, any>

// TODO show time remaining on auth token ± a few seconds and lockout form if times out
const FormComponent: React.SFC<RegisterFormFormProps & RegisterFormProps> =
    ({ handleSubmit, username, error, valid, anyTouched }) =>
        <form className="register-form" onSubmit={handleSubmit}>
            <InputField name="username" label="Username" placeholder={username} required disabled>
                <div className="pt-form-helper-text">This is taken from your authorised account</div>
            </InputField>
            <InputField name="email" label="Email" required />
            <InputField name="password" label="Password" required isPassword />
            <InputField name="repeat" label="Repeat Password" required isPassword />

            <button
                type="submit"
                className={`pt-button ${valid || !anyTouched ? 'pt-intent-success' : 'pt-intent-danger'}`}
                disabled={anyTouched && !valid}
                onClick={handleSubmit}
            >Register</button>
            {
                error && <div className="pt-callout pt-intent-danger pt-icon-warning-sign">
                    { error }
                </div>
            }
        </form>;

const WithBoundFormComponent: React.SFC<RegisterFormGraphqlProps & RegisterFormProps> =
    reduxForm<RegisterFormData, RegisterFormGraphqlProps, any>({
        form: 'register',
        validate: values => {
            const errors: FormErrors<RegisterFormData> = {};

            if (!values.email || !/^^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/.test(values.email)) {
                errors.email = 'Invalid email provided';
            }

            if (!values.password) {
                errors.password = 'Please input your chosen password'
            } else if (values.password.length < 8) {
                errors.password = 'Must be at least 8 characters long';
            } else if (!/[a-z]+/.test(values.password)) {
                errors.password = 'Must contain at least 1 lower case character';
            } else if (!/[A-Z]+/.test(values.password)) {
                errors.password = 'Must contain at least 1 upper case character';
            } else if (!/[0-9]+/.test(values.password)) {
                errors.password = 'Must contain at least 1 digit';
            } else if (!/[^a-zA-Z0-9]+/.test(values.password)){
                errors.password = 'Must contain at least 1 special character';
            }

            if (values.repeat !== values.password) {
                errors.repeat = 'Does not match supplied password';
            }

            return errors;
        },
        onSubmit: (values, dispatch, props) => {
            return props.mutate!({
                variables: {
                    token: props.token,
                    email: values.email,
                    password: values.password
                } as RegisterMutationVariables
            }).catch(err => {
                const errors: FormErrors<RegisterFormData> = {};

                if (isApolloError(err)) {
                    errors._error = err.graphQLErrors[0].message;
                }

                throw new SubmissionError(errors)
            });
            // TODO something useful on success
        }
    })(FormComponent);

export const RegisterForm: React.ComponentClass<RegisterFormProps> =
    graphql<RegisterFormGraphqlProps, RegisterFormProps>(registerMutation)(WithBoundFormComponent);

