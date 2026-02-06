'use client';

import { ContentCopy, Delete, OpenInNew } from "@mui/icons-material";
import { Box, Button, Card, CardContent, Checkbox, Divider, Grid, IconButton, Paper, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography } from "@mui/material";
import Image from "next/image";
import { useEffect, useState } from "react";

type ShortenedURL = {
  alias: string;
  fullUrl: string
  shortUrl: string
}


export default function Home() {

  const [urls, setUrls] = useState<ShortenedURL[]>([]);

  useEffect(() => {
    fetch("http://localhost:8080/urls")
      .then(res => res.json())
      .then(data => setUrls(data))
  });


  return (
    <Grid padding={2}>
      <Typography variant="h6" component="div" marginBottom={2}>Shorten a new URL</Typography>
      <Box component="form" marginBottom={2}>
        <Stack spacing={2} maxWidth={300}>
          <TextField label="URL to shorten" required></TextField>
          <TextField label="alias" helperText="Leave blank for a random alias" ></TextField>
          <div>
            <Button type="submit" variant="contained">Shorten</Button>
          </div>
        </Stack>
      </Box>
      <Divider />
      <Typography variant="h6" padding={1}> Shortened URLs</Typography>
      {/* Create table showing the Shortened URLs */}
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell padding="checkbox">
                <Checkbox
                  color="primary"
                />
              </TableCell>
              <TableCell>Full URL</TableCell>
              <TableCell>Shortened URL</TableCell>
              <TableCell align="right"></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {urls.length === 0 ? (
              <TableRow>
                <TableCell colSpan={3} align="center">
                  No urls have been shortened yet
                </TableCell>
              </TableRow>
            ) : (
            urls.map((url) => (
              <TableRow
                key={url.alias}
              >
                <TableCell>{url.fullUrl}</TableCell>
                <TableCell>{url.shortUrl}</TableCell>
                <TableCell>
                  <IconButton>
                    <ContentCopy />
                  </IconButton>
                  <IconButton>
                    <OpenInNew />
                  </IconButton>
                  <IconButton>
                    <Delete />
                  </IconButton>
                </TableCell>

              </TableRow>
            )))}
          </TableBody>

        </Table>
      </TableContainer>

    </Grid>
        
  );
}
