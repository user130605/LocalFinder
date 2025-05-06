import { Container, Grid, TextField, Paper, Button, Typography, Box, Link as MuiLink } from "@mui/material";
import { Link } from 'react-router-dom';

function LoginPage() {
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