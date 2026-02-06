'use client';

import { ContentCopy, Delete, OpenInNew } from "@mui/icons-material";
import { Alert, Box, Button, Divider, Grid, IconButton, Paper, Snackbar, Stack, Table, TableBody, TableCell, TableContainer, TableFooter, TableHead, TablePagination, TablePaginationActions, TableRow, TextField, Tooltip, Typography } from "@mui/material";

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
  const [page, setPage] = useState(0)
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const [urlTouched, setUrlTouched] = useState(false);
  const [aliasTouched, setAliasTouched] = useState(false);
  const [urlError, setUrlError] = useState<string | null>(null);
  const [aliasError, setAliasError] = useState<string | null>(null);

  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [errorMessageOpen, setErrorMessageOpen] = useState(false);

  const [showCopiedMessage, setShowCopiedMessage] = useState(false);

  const ALIAS_REGEX = /^[a-zA-Z0-9_-]+$/;

  const fetchUrls = () => {
    fetch("http://localhost:8080/urls")
      .then(res => res.json())
      .then(setUrls);
  }

  useEffect(() => {
    fetchUrls();
  }, []);

  const onUrlBlur = () => {
    setUrlTouched(true)
    checkUrlValidation(url);
  }

  const checkUrlValidation = (value: string) => {
    try {
      new URL(value);
      setUrlError(null);
    } catch {
      setUrlError("Please enter a valid url");
    }
  }

  const onUrlChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value
    setUrl(value);
    if (urlTouched) {
      checkUrlValidation(value);
    }
  }

  const onAliasBlur = () => {
    setAliasTouched(true);
    checkAliasValidation(alias);
  }

  const isReadyForSubmit = () => {
    // No URL means we can't submit
    if (!url) {
      return false;
    }

    try {
      new URL(url);
    } catch {
      return false // Invalid URL
    }

    // URL is valid
    
    if (!alias || (aliasTouched && aliasError === null)) {
      return true;
    }

    return false;
  }

  const checkAliasValidation = (value: string) => {
    if (!!value) {
      // Check conforms to agreed regex
      if (!ALIAS_REGEX.test(value)) {
        setAliasError("Alias must only contain letters, numbers, hyphens and underscores");
        return;
      }
      // check for other aliases
      if (urls.find(url => url.alias === value) !== undefined) {
        setAliasError("This alias already exists");
        return;
      }
    }

    setAliasError(null);
  }

  const onAliasChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value
    setAlias(value);
    if (aliasTouched) {
      checkAliasValidation(value);
    }
  }

  const copyShortUrl = async (url: string) => {
    await navigator.clipboard.writeText(url);
    setShowCopiedMessage(true);
    
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

  const handleChangePage = (
    _: React.MouseEvent<HTMLButtonElement> | null,
    newPage: number,
  ) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };


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
      setUrlTouched(false)
      setAliasTouched(false);
      setUrlError(null);
      setAliasError(null);
      fetchUrls();
    } else if (resp.status === 400) { // Client error
      // Grab the message body
      const json = await resp.json();
      setErrorMessage(json.message || "There was an issue with your request");
      setErrorMessageOpen(true);
    } else {
      // Log to the console for developers to look at
      console.log(resp);
      setErrorMessage("Unable to save the URL")
      setErrorMessageOpen(true);
    }
  }


  return (
    <Grid padding={2}>
      <Typography variant="h6" component="div" marginBottom={2}>Shorten a new URL</Typography>
      <Box onSubmit={shortenURL} component="form" marginBottom={2}>
        <Stack spacing={2} maxWidth={300}>
          <TextField 
            label="URL to shorten" 
            value={url} 
            onChange={onUrlChange}
            onBlur={onUrlBlur}
            error={!!urlError}
            helperText={urlError || " "}
            required></TextField>
          <TextField 
            label="alias" 
            value={alias} 
            onChange={onAliasChange}
            onBlur={onAliasBlur}
            error={!!aliasError} 
            helperText={aliasError || "Leave blank for a random alias"}
            ></TextField>
          <div>
            <Button type="submit" 
              disabled={!isReadyForSubmit()} 
              variant="contained">Shorten</Button>
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
            urls
              .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
              .map((url) => (
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
          <TableFooter >
            <TableRow>
              <TablePagination 
                colSpan={3}
                count={urls.length}
                page={page}
                rowsPerPage={rowsPerPage}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
                ActionsComponent={TablePaginationActions}
              />
            </TableRow>

          </TableFooter>
        </Table>
      </TableContainer>
      {/* Alerts */}
      <Snackbar
        anchorOrigin={{ vertical: "top", horizontal: "right"}}
        autoHideDuration={5000}
        onClose={() => setErrorMessageOpen(false)}
        open={errorMessageOpen}
      >
        <Alert
          onClose={() => setErrorMessageOpen(false)}
          severity="error"
        >
          {errorMessage}
        </Alert>
      </Snackbar>
      <Snackbar 
        open={showCopiedMessage} 
        autoHideDuration={1000} 
        onClose={() => setShowCopiedMessage(false)}
        message="URL copied"></Snackbar>

      
    </Grid>
        
  );
}
