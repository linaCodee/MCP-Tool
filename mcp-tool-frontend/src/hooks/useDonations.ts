import {useCallback, useEffect, useState} from 'react';
import type { CreateDonationBatchRequest, DonationBatchResponse } from '../api/types/donation.ts';
import useSnackbar from "./useSnackbar.ts";
import donationApi from "../api/donationApi.ts";

/**
 * TODO(student): Drive the donation workflow for the DonationsPage: list the
 * batches (donationApi.findAll) and expose create/approve/submit actions,
 * re-fetching after every mutation, with loading/error state. Mirror the
 * searchRunsProvider pattern.
 */
const useDonations = () => {
  const {showSnackbar}=useSnackbar();

  const [donations,setDonations] = useState<DonationBatchResponse[]>([]);
  const [loading,setLoading] = useState<boolean>(false);
  const [error,setError]=useState<string|null>(null);

  const fetchDonations=useCallback(async ()=>{
    setLoading(true);
    setError(null);


    try {
      const response=await donationApi.findAll();
      setDonations(response.data);
    } catch (err){
      const message=err instanceof Error ? err.message : "Failed to load donations.";
      setError(message);
      showSnackbar(message, 'error');
    } finally {
      setLoading(false);
    }
  },[showSnackbar]);

  const onCreate = async (data: CreateDonationBatchRequest) => {
    try {
      await donationApi.add(data);
      showSnackbar("Donation batch created successfully.",'success');
      await fetchDonations();
    } catch (err){
      showSnackbar(err instanceof Error ? err.message : "Failed to create donation batch",'error');
    }
  };
  const onApprove = async (id: number) => {
    try {
      await donationApi.approve(id.toString());
      showSnackbar("Donation batch approved successfully.",'success');
      await fetchDonations();
    } catch (err){
      showSnackbar(err instanceof Error ? err.message : "Failed to approve donation batch.",'error');
    }
  };
  const onSubmit = async (id: number) => {
    try {
      await donationApi.submit(id.toString());
      showSnackbar("Donation batch submitted successfully.",'success');
      await fetchDonations();
    } catch (err){
      showSnackbar(err instanceof Error ? err.message : "Failed to submit donation batch.",'error');
    }
  };

  useEffect(()=>{
    void fetchDonations();
  },[fetchDonations]);

  return { donations, loading, onCreate,error, onApprove, onSubmit, refresh: fetchDonations };
};

export default useDonations;
