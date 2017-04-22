import { Form, Icon, Input, Button } from 'antd';
const FormItem = Form.Item;

class RegisterFormComponent extends React.Component {
    formItemLayout = {
        labelCol: {
            xs: { span: 24 },
            sm: { span: 6 },
        },
        wrapperCol: {
            xs: { span: 24 },
            sm: { span: 14 },
        },
    };

    emailRules = [
        { required: true, message: 'An email must be provided' },
        { pattern: /^^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/, message: 'Invalid email provided' }
    ];

    passwordRules = [
        { required: true, message: 'Please input your chosen password' },
        { pattern: /[a-z]+/, message: 'Must contain at least 1 lower case character'},
        { pattern: /[A-Z]+/, message: 'Must contain at least 1 upper case character'},
        { pattern: /[0-9]+/, message: 'Must contain at least 1 digit'},
        { pattern: /[^a-zA-Z0-9]+/, message: 'Must contain at least 1 special character'},
        { min: 8, message: 'Must contain at least 8 characters'}
    ];

    repeatPasswordRules = [
        { required: true, validator: (rule, value, callback) => {
            if (value !== this.props.form.getFieldValue('password')) {
                callback('Password does not match');
            } else {
                callback();
            }
        }}
    ];

    handleSubmit = (e) => {
        e.preventDefault();
        this.props.form.validateFieldsAndScroll((err, values) => {
            if (!err) {
                console.log('Received values of form: ', values);

                fetch('/register/finalise', {
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

    render = () => <Form className="login-form" onSubmit={this.handleSubmit}>
        <FormItem {...this.formItemLayout} hasFeedback label="Email">
            {this.props.form.getFieldDecorator('email', {rules: this.emailRules})(
                <Input prefix={<Icon type="user" style={{fontSize: 13}}/>} placeholder="Email"/>
            )}
        </FormItem>
        <FormItem {...this.formItemLayout} hasFeedback label="Password">
            {this.props.form.getFieldDecorator('password', {rules: this.passwordRules})(
                <Input prefix={<Icon type="lock" style={{fontSize: 13}}/>} type="password" placeholder="Password"/>
            )}
        </FormItem>
        <FormItem {...this.formItemLayout} hasFeedback label="Repeat Password">
            {this.props.form.getFieldDecorator('confirm', {rules: this.repeatPasswordRules})(
                <Input prefix={<Icon type="lock" style={{fontSize: 13}}/>} type="password" placeholder="Repeat Password"/>
            )}
        </FormItem>
        <FormItem {...this.formItemLayout}>
            <Button type="primary" htmlType="submit" size="large">Register</Button>
        </FormItem>
    </Form>
}

export const RegisterForm = Form.create()(RegisterFormComponent);

export default RegisterForm;