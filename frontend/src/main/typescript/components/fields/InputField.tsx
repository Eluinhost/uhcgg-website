import * as React from 'react';
import {BaseFieldProps, Field, WrappedFieldMetaProps, WrappedFieldProps} from "redux-form";

const errorClass = (meta: WrappedFieldMetaProps<any>) => {
    if (meta.error) {
        return 'pt-intent-danger';
    }

    if (meta.warning) {
        return 'pt-intent-warning';
    }

    return ''
};

export interface InputFieldProps extends BaseFieldProps {
    label: string,
    required: boolean,
    placeholder?: string,
    isPassword?: boolean,
    disabled?: boolean
}

export class InputField extends React.PureComponent<InputFieldProps> {
    renderInputField = (wrapped: WrappedFieldProps<any>) =>
        <div className={`pt-form-group ${errorClass(wrapped.meta)}`}>
            <label className="pt-label">
                {this.props.label}{this.props.required && '*'}
            </label>
            <div className="pt-form-content">
                <input
                    {...wrapped.input}
                    className="pt-input"
                    placeholder={this.props.placeholder || this.props.label}
                    type={this.props.isPassword ? "password" : "text"}
                    required={this.props.required}
                    disabled={this.props.disabled}
                />
                { this.props.children }

                {
                    wrapped.meta.touched
                    &&
                    (
                        (wrapped.meta.error && <div className="pt-form-helper-text">{ wrapped.meta.error }</div>)
                        ||
                        (wrapped.meta.warning && <div className="pt-form-helper-text">{ wrapped.meta.warning }</div>)
                    )
                }
            </div>
        </div>;

    render() {
        return <Field { ...this.props } component={this.renderInputField} />;
    }
}
