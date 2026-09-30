import {useCallback, useEffect, useState} from 'react';
import type { PageResponse, ResourceFilter, ResourceResponse } from '../api/types/resource.ts';
import useSnackbar from "./useSnackbar.ts";
import resourceApi from "../api/resourceApi.ts";

/**
 * TODO(student): Load a page of resources (resourceApi.findAll) for the
 * ResourcesPage, re-fetching whenever the filter or page changes, with
 * loading/error state, a delete action (resourceApi.delete) and an analyze
 * action (resourceApi.analyze). Mirror the searchRunsProvider pattern.
 */
const useResources =
    (
        filter: ResourceFilter,
        page: number,
        size: number
    ) => {

  const {showSnackbar}=useSnackbar();

  const [resources, setResources] = useState<PageResponse<ResourceResponse> | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  const fetchResources=useCallback(async ()=>{
    setLoading(true);

    try{
      const response=await resourceApi.findAll(filter,page,size);
      setResources(response.data);
    } catch (err){
      showSnackbar(err instanceof Error ? err.message : "Failed to load resources.", "error");
    } finally {
      setLoading(false);
    }
  },[filter,page,size,showSnackbar]);

  useEffect(()=>{
    void fetchResources();
  },[fetchResources]);

  const onDelete = async (id: number) => {
    try{
      await resourceApi.delete(id.toString());
      showSnackbar("Resource deleted successfully","success");
      await fetchResources();
    } catch (err){
      showSnackbar(
          err instanceof Error ? err.message : "Failed to delete resource.",'error'
      );
    }
  };
  const onAnalyze = async (id: number) => {
    try {
      await resourceApi.analyze(id.toString());

      showSnackbar("Resource analyzed successfully.",'success');
      await fetchResources();
    } catch (err){
      showSnackbar(err instanceof Error ? err.message : "Failed to delete resource.",'error');
    }
  };

  return { resources, loading, onDelete, onAnalyze, refresh: fetchResources };
};

export default useResources;
