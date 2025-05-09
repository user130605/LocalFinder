import { Container, Grid, TextField, Paper, Button, Typography, Box, Link as MuiLink } from "@mui/material";
import { Link, useNavigate } from 'react-router-dom';
import { useState } from "react";
import * as Yup from 'yup';
import axios from 'axios';

const validationSchema = Yup.object({
  email: Yup.string().email('올바른 이메일 주소를 입력하세요').required('이메일을 입력하세요'),
  password: Yup.string().min(4, '비밀번호는 최소 4자 이상이어야 합니다.').required('비밀번호를 입력하세요')
});

function LoginPage() {
  const navigate = useNavigate();
  const [values, setValues] = useState({ email: '', password: '' });
  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setErrors({ ...errors, [name]: '' });
    setValues({ ...values, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await validationSchema.validate(values, { abortEarly: false });
      const response = await axios.post('http://localhost:8000/user-service/login', values);
      console.log('로그인 성공:', response.data);
      navigate('/');
    } catch (err) {
      if (err.name === 'ValidationError') {
        const errorMessages = {};
        err.inner.forEach((error) => {
          errorMessages[error.path] = error.message;
        });
        setErrors(errorMessages);
      } else {
        console.error('로그인 실패:', err.response?.data || err.message);
      }
    }
  };

  return(
    <Container component="main" maxWidth="xs">
      <Paper
        elevation={3}
        sx={{
            marginTop: 8,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            padding: 3
        }}
      >
        <Typography component="h1" variant="h5" mb={2}>
          로그인
        </Typography>
            <Grid container spacing={2} justifyContent={"center"}>
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
                        error={Boolean(errors.email)}
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
                        error={Boolean(errors.password)}
                        helperText={errors.password}
                    />
                </Grid>
            </Grid>
            <Button
                type='submit'
                fullWidth
                variant='contained'
                sx={{ mt: 3, mb: 1 }}
                >
                로그인
            </Button>
            <Box mt={1}>
              <MuiLink component={Link} to="/register" variant="body2">회원가입</MuiLink>
            </Box>
      </Paper>
    </Container>
  )
}

export default LoginPage;