'use client';

import { ContentCopy, Delete, OpenInNew } from "@mui/icons-material";
import { Box, Button, Checkbox, Divider, Grid, IconButton, Paper, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Tooltip, Typography } from "@mui/material";

import { useEffect, useState } from "react";

type ShortenedURL = {
  alias: string;
  fullUrl: string
  shortUrl: string
}


export default function Home() {

  const [urls, setUrls] = useState<ShortenedURL[]>([]);
  const [url, setUrl] = useState("");
  const [alias, setAlias] = useState("");

  const fetchUrls = () => {
    fetch("http://localhost:8080/urls")
      .then(res => res.json())
      .then(setUrls);
  }

  useEffect(() => {
    fetchUrls();
  }, []);


  const copyShortUrl = async (url: string) => {
    await navigator.clipboard.writeText(url);
    
  }

  const deleteAlias = async (alias: string) => {
    const encoded = encodeURIComponent(alias);
    const resp = await fetch(`http://localhost:8080/${encoded}`, {
      method: 'DELETE'
    });

    if (resp.ok) {
      fetchUrls();
    }
  }


  const shortenURL = async (e: React.SubmitEvent) => {
    e.preventDefault();

    const body = {
      fullUrl: url,
      customAlias: alias.length === 0 ? undefined : alias
    }

    const resp = await fetch('http://localhost:8080/shorten', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(body)
    });

    if (resp.ok) {
      setUrl("")
      setAlias("")
      fetchUrls();
    }
  }


  return (
    <Grid padding={2}>
      <Typography variant="h6" component="div" marginBottom={2}>Shorten a new URL</Typography>
      <Box onSubmit={shortenURL} component="form" marginBottom={2}>
        <Stack spacing={2} maxWidth={300}>
          <TextField label="URL to shorten" value={url} onChange={(e) => setUrl(e.target.value)} required></TextField>
          <TextField label="alias" value={alias} onChange={(e) => setAlias(e.target.value)} helperText="Leave blank for a random alias" ></TextField>
          <div>
            <Button type="submit" variant="contained">Shorten</Button>
          </div>
        </Stack>
      </Box>
      <Divider />
      <Typography variant="h6" padding={1}> Shortened URLs</Typography>
      {/* Create table showing the Shortened URLs */}
      <TableContainer component={Paper}>
        <Table sx={{ tableLayout: "fixed", width: "100%" }}>
          <TableHead>
            <TableRow>
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
                <TableCell >
                  <Tooltip title={url.fullUrl}>
                    <Box
                      component="span"
                      sx={{
                        display: "inline-block",
                        width: "100%",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                        whiteSpace: "nowrap",
                      }}
                    >
                      {url.fullUrl}
                    </Box>
                  </Tooltip>
                </TableCell>
                <TableCell>
                  <Tooltip title={url.shortUrl}>
                    <Box
                      component="span"
                      sx={{
                        display: "inline-block",
                        width: "100%",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                        whiteSpace: "nowrap",
                      }}
                    >
                      {url.shortUrl}
                    </Box>
                  </Tooltip>
                </TableCell>
                <TableCell align="right">
                  <Tooltip title="Copy short URL">
                    <IconButton  onClick={() => copyShortUrl(url.shortUrl)}>
                      <ContentCopy />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Open in new tab">
                    <IconButton onClick={() => window.open(url.shortUrl, "_blank", "noopener,noreferrer")}>
                      <OpenInNew />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Remove alias">
                    <IconButton onClick={() => deleteAlias(url.alias)} >
                      <Delete />
                    </IconButton>
                  </Tooltip>
                </TableCell>

              </TableRow>
            )))}
          </TableBody>

        </Table>
      </TableContainer>

    </Grid>
        
  );
}
