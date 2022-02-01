export const Errors = {
  FIELD_IS_REQUIRED: 'Field is required',

  MAX_LENGTH: (length) => `Field must be maximum ${length} characters`,
  MIN_LENGTH: (length) => `Field must be minimum ${length} characters`,

  INCORRECT_VALUE: (min, max) => `Field out of bounds (${min}-${max})`,
  INCORRECT_TYPE: (type) => `Field must be ${type} type`,

  MIN_DATE: (date) => `Use date greater than ${date}`,
  MAX_DATE: (date) => `Use date less or equal to ${date}`,

  NO_DIGITS: 'Can\'t have digits',
  INVALID_EMAIL: 'Invalid email',
  NOT_CONFIRMED: 'Password doesn\'t match',

  INVALID_ADDRESS: 'Address is not valid'
};
