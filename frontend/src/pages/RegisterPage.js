import { Container, Grid, TextField, Paper, Button, Typography } from "@mui/material";
import axios from 'axios';
import * as Yup from 'yup';
import { useState } from "react";
import { useNavigate } from 'react-router-dom';

const validationSchema = Yup.object({
  email: Yup.string().email('올바른 이메일 주소를 입력하세요').required('이메일을 입력하세요'),
  password: Yup.string().min(8, '비밀번호는 최소 8자 이상이어야 합니다.').required('비밀번호를 입력하세요'),
  nickname: Yup.string().required('닉네임을 입력하세요')
})

const RegisterPage = () => {
  const navigation = useNavigate();
  const [values, setValues] = useState({email: '', password: '', nickname: ''})
  const [errors, setErrors] = useState({});
  const handleChange = (e) => {
    const { name, value } = e.target;
    setErrors({
      ...errors,
      [name] : ''
    })
    setValues({
      ...values,
      [name] : value
    });
  };

  const handleSubmit = async(e) => {
      e.preventDefault();
      await validationSchema.validate(values, { abortEarly : false}).then((valid) => {
        axios.post('http://localhost:8000/user-service/create', values)
          .then(response => {
            console.log('회원가입 성공:', response.data);
            navigation('/home');
          })
          .catch(error => {
            console.error('회원가입 실패:', error);
          });
      }).catch((err) => {
        console.log('err : ', err)
        const errorMessages = {};
        err.inner.forEach((error) => {
          errorMessages[error.path] = error.message;
        });
        setErrors(errorMessages);
      });
  }

  return (
    <Container component="main" maxWidth="xs">
      <Paper
          elevation={3}
          sx={{
              marginTop: 8,
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              padding: 3
          }}>
        <Typography component="h1" variant="h5" mb={2}>
          회원가입
        </Typography>
        <form
            noValidate
            onSubmit={handleSubmit}
            sx={{ width: '100%', marginTop:3}}
        >
            <Grid container spacing={2}>
                <Grid item sx={12}>
                    <TextField
                        required
                        fullWidth
                        id="email"
                        label="이메일 주소"
                        name="email"
                        autoComplete='email'
                        autoFocus
                        value={values.email}
                        onChange={handleChange}
                        onError={Boolean(errors.email)}
                        helperText={errors.email}
                    />
                </Grid>
                <Grid item sx={12}>
                    <TextField
                        required
                        fullWidth
                        id="password"
                        label="비밀번호"
                        name="password"
                        type='password'
                        autoComplete='password'
                        value={values.password}
                        onChange={handleChange}
                        onError={Boolean(errors.password)}
                        helperText={errors.password}
                    />
                </Grid>
                <Grid item sx={12}>
                    <TextField
                        required
                        fullWidth
                        id="nickname"
                        label="닉네임"
                        name="nickname"
                        autoFocus
                        value={values.nickname}
                        onChange={handleChange}
                        onError={Boolean(errors.nickname)}
                        helperText={errors.nickname}
                    />
                </Grid>
            </Grid>
            <Button
                type='submit'
                fullWidth
                variant='contained'
                sx={{ mt: 3, mb: 1 }}
                >
                가입하기
            </Button>

        </form>
      </Paper>
    </Container>
  );
};

export default RegisterPage;