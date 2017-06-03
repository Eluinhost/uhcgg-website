import * as React from 'react';
import { Form, Icon, Input, Button } from 'antd';
import { FormItemColOption } from 'antd/lib/form/FormItem';
const FormItem = Form.Item;

const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
    } as FormItemColOption,
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
    } as FormItemColOption,
};

const emailRules = [
    {
        required: true,
        message: 'An email must be provided'
    },
    {
        pattern: /^^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/,
        message: 'Invalid email provided'
    }
];

const passwordRules = [
    { required: true, message: 'Please input your chosen password' },
    { pattern: /[a-z]+/, message: 'Must contain at least 1 lower case character'},
    { pattern: /[A-Z]+/, message: 'Must contain at least 1 upper case character'},
    { pattern: /[0-9]+/, message: 'Must contain at least 1 digit'},
    { pattern: /[^a-zA-Z0-9]+/, message: 'Must contain at least 1 special character'},
    { min: 8, message: 'Must contain at least 8 characters'}
];

const repeatPasswordRules = (form: any) => [
    { required: true, validator: (rule: any, value: any, callback: any) => {
        if (value !== form.getFieldValue('password')) {
            callback('Passwords do not match');
        } else {
            callback();
        }
    }}
];

const handleSubmit = (form: any) => (e: any) => {
    e.preventDefault();

    form.validateFieldsAndScroll((err: any, values: any) => {
        if (!err) {
            console.log('Received values of form: ', values);

            fetch('/api/register', {
                method: 'POST',
                body: JSON.stringify(values),
                credentials: 'same-origin',
                headers: {
                    'Content-Type': 'application/json'
                }
            })

            // TODO handle response
        }
    });
};

interface RegisterFormProps {
    username: String,
    form: any
}

class RegisterFormComponent extends React.PureComponent<RegisterFormProps, any> {
    render() {
        return <Form className="login-form" onSubmit={handleSubmit(this.props.form)}>
            <FormItem {...formItemLayout} label="Username">
                <Input prefix={<Icon type="user" style={{fontSize: 13}}/>} value={this.props.username} disabled={true} />
            </FormItem>
            <FormItem {...formItemLayout} label="Email">
                {this.props.form.getFieldDecorator('email', {rules: emailRules})(
                    <Input prefix={<Icon type="user" style={{fontSize: 13}}/>} placeholder="Email"/>
                )}
            </FormItem>
            <FormItem {...formItemLayout} label="Password">
                {this.props.form.getFieldDecorator('password', {rules: passwordRules})(
                    <Input prefix={<Icon type="lock" style={{fontSize: 13}}/>} type="password" placeholder="Password"/>
                )}
            </FormItem>
            <FormItem {...formItemLayout} label="Confirm Password">
                {this.props.form.getFieldDecorator('confirm', {rules: repeatPasswordRules(this.props.form)})(
                    <Input prefix={<Icon type="lock" style={{fontSize: 13}}/>} type="password" placeholder="Confirm Password"/>
                )}
            </FormItem>
            <FormItem {...formItemLayout}>
                <Button type="primary" htmlType="submit" size="large">Register</Button>
            </FormItem>
        </Form>;
    }
}

export const RegisterForm = Form.create()(RegisterFormComponent);