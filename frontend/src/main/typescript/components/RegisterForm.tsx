import * as React from 'react';
import {DefaultChildProps, graphql, MutationFunc} from 'react-apollo';
import { RegisterMutation, RegisterMutationVariables } from '../graphql';

const registerMutation = require('../../graphql/register.graphql');

// const formItemLayout = {
//     labelCol: {
//         xs: { span: 24 },
//         sm: { span: 6 },
//     } as FormItemColOption,
//     wrapperCol: {
//         xs: { span: 24 },
//         sm: { span: 14 },
//     } as FormItemColOption,
// };
//
// const emailRules = [
//     {
//         required: true,
//         message: 'An email must be provided'
//     },
//     {
//         pattern: /^^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/,
//         message: 'Invalid email provided'
//     }
// ];
//
// const passwordRules = [
//     { required: true, message: 'Please input your chosen password' },
//     { pattern: /[a-z]+/, message: 'Must contain at least 1 lower case character'},
//     { pattern: /[A-Z]+/, message: 'Must contain at least 1 upper case character'},
//     { pattern: /[0-9]+/, message: 'Must contain at least 1 digit'},
//     { pattern: /[^a-zA-Z0-9]+/, message: 'Must contain at least 1 special character'},
//     { min: 8, message: 'Must contain at least 8 characters'}
// ];
//
// const repeatPasswordRules = (form: WrappedFormUtils) => [
//     { required: true, validator: (rule: any, value: any, callback: any) => {
//         if (value !== form.getFieldValue('password')) {
//             callback('Passwords do not match');
//         } else {
//             callback();
//         }
//     }}
// ];
//
// const handleSubmit = (form: WrappedFormUtils, mutate: MutationFunc<RegisterMutation>, token: string) => (e: any) => {
//     e.preventDefault();
//
//     form.validateFieldsAndScroll({}, (err: any, values: any) => {
//         if (!err) {
//             console.log('Received values of form: ', values);
//
//             const variables: RegisterMutationVariables = {
//                 password: values.password,
//                 token,
//                 email: values.email
//             };
//
//             mutate({ variables })
//
//             // TODO handle response
//         }
//     });
// };
//
export interface RegisterFormProps {
    username: string,
    token: string
}
//
// export const RegisterFormComponent: React.SFC<DefaultChildProps<RegisterFormProps & FormComponentProps, RegisterMutationVariables>> =
//     ({ form, username, mutate, token }) =>
//         <Form className="login-form" onSubmit={handleSubmit(form, mutate as MutationFunc<RegisterMutation>, token)}>
//             <Form.Item {...formItemLayout} label="Username">
//                 <Input prefix={<Icon type="user" style={{fontSize: 13}}/>} value={username} disabled={true} />
//             </Form.Item>
//             <Form.Item {...formItemLayout} label="Email">
//                 {form.getFieldDecorator('email', {rules: emailRules})(
//                     <Input prefix={<Icon type="user" style={{fontSize: 13}}/>} placeholder="Email"/>
//                 )}
//             </Form.Item>
//             <Form.Item {...formItemLayout} label="Password">
//                 {form.getFieldDecorator('password', {rules: passwordRules})(
//                     <Input prefix={<Icon type="lock" style={{fontSize: 13}}/>} type="password" placeholder="Password"/>
//                 )}
//             </Form.Item>
//             <Form.Item {...formItemLayout} label="Confirm Password">
//                 {form.getFieldDecorator('confirm', {rules: repeatPasswordRules(form)})(
//                     <Input prefix={<Icon type="lock" style={{fontSize: 13}}/>} type="password" placeholder="Confirm Password"/>
//                 )}
//             </Form.Item>
//             <Form.Item {...formItemLayout}>
//                 <Button type="primary" htmlType="submit" size="large">Register</Button>
//             </Form.Item>
//         </Form>;
//
// export const BoundRegisterFormComponent: React.ComponentClass<RegisterFormProps & FormComponentProps> =
//     graphql<RegisterMutationVariables, RegisterFormProps & FormComponentProps>(registerMutation)(RegisterFormComponent);
//
// export const RegisterForm: React.SFC<RegisterFormProps> = Form.create()(BoundRegisterFormComponent);

export const RegisterForm: React.SFC<RegisterFormProps> = () => <span> WIP </span>;