import { Form, Icon, Input, Button } from 'antd';
const FormItem = Form.Item;
import { pure } from 'recompose';

const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
    },
};

const emailRules = [
    { required: true, message: 'An email must be provided' },
    { pattern: /^^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/, message: 'Invalid email provided' }
];

const passwordRules = [
    { required: true, message: 'Please input your chosen password' },
    { pattern: /[a-z]+/, message: 'Must contain at least 1 lower case character'},
    { pattern: /[A-Z]+/, message: 'Must contain at least 1 upper case character'},
    { pattern: /[0-9]+/, message: 'Must contain at least 1 digit'},
    { pattern: /[^a-zA-Z0-9]+/, message: 'Must contain at least 1 special character'},
    { min: 8, message: 'Must contain at least 8 characters'}
];

const repeatPasswordRules = form => [
    { required: true, validator: (rule, value, callback) => {
        if (value !== form.getFieldValue('password')) {
            callback('Passwords do not match');
        } else {
            callback();
        }
    }}
];

const handleSubmit = form => e => {
    e.preventDefault();

    form.validateFieldsAndScroll((err, values) => {
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

const RegisterFormComponent = pure(({ form, username }) =>  <Form className="login-form" onSubmit={handleSubmit(form)}>
    <FormItem { ...formItemLayout} label="Username">
        <Input prefix={<Icon type="user" style={{fontSize: 13}}/>} value={username} disabled={true} />
    </FormItem>
    <FormItem {...formItemLayout} label="Email">
        {form.getFieldDecorator('email', {rules: emailRules})(
            <Input prefix={<Icon type="user" style={{fontSize: 13}}/>} placeholder="Email"/>
        )}
    </FormItem>
    <FormItem {...formItemLayout} label="Password">
        {form.getFieldDecorator('password', {rules: passwordRules})(
            <Input prefix={<Icon type="lock" style={{fontSize: 13}}/>} type="password" placeholder="Password"/>
        )}
    </FormItem>
    <FormItem {...formItemLayout} label="Confirm Password">
        {form.getFieldDecorator('confirm', {rules: repeatPasswordRules(form)})(
            <Input prefix={<Icon type="lock" style={{fontSize: 13}}/>} type="password" placeholder="Confirm Password"/>
        )}
    </FormItem>
    <FormItem {...formItemLayout}>
        <Button type="primary" htmlType="submit" size="large">Register</Button>
    </FormItem>
</Form>);

export const RegisterForm = Form.create()(RegisterFormComponent);

export default RegisterForm;