using Kiwi.Website.Portal.Models;
using Kiwi.Website.Portal.Models.ApiModels;
using Kiwi.Website.Portal.Models.Constants;
using Microsoft.AspNet.Identity;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;
using System.Data.Entity;
using Microsoft.Web.Http;

namespace Kiwi.Website.Portal.Controllers.ApiControllers
{
    [ApiVersion("1.0")]
    [RoutePrefix("api/v{version:apiVersion}/User")]
    [Authorize]
    public class UserController : ApiController
    {
        [HttpGet]
        [Route("Profile")]
        [AllowAnonymous]
        public IHttpActionResult GetProfile()
        {
            var userId = User.Identity.GetUserId<int>();
            using (var db = new KiwiEntities())
            {
                var profile = db.Users
                    .Where(x => x.Id == userId)
                    .AsEnumerable()
                    .Select(x => new
                    {
                        Id = x.Id,
                        Username = x.Username,
                        FirstName = x.Contact.FirstName,
                        LastName = x.Contact.LastName,
                        GenderId = x.Contact.GenderId,
                        DateOfBirth = x.Contact.DateOfBirth == null ? string.Empty : x.Contact.DateOfBirth.Value.ToString(ApiConstants.DateFormat),
                        Mobile = x.Contact.Mobile,
                        Landline = x.Contact.Landline,
                        Email = x.Contact.Email,

                        StreetNum = x.Contact.StreetNum,
                        Address = x.Contact.Address,
                        Suburb = x.Contact.Suburb,
                        City = x.Contact.City,
                        Postcode = x.Contact.Postcode,
                        Country = x.Contact.Country,

                        BillingStreetNum = x.Contact.BillingStreetNum,
                        BillingAddress = x.Contact.BillingAddress,
                        BillingSuburb = x.Contact.BillingSuburb,
                        BillingCity = x.Contact.BillingCity,
                        BillingPostcode = x.Contact.BillingPostcode,
                        BillingCountry = x.Contact.BillingCountry,

                        OscarNum = x.Contact.OscarNum,
                        ReviewDate = x.Contact.ReviewDate,

                        ProfileImage = db.Media.Where(m => m.EntityId == userId && m.EntityType == EntityTypeName.User).Select(m => m.NewFileName).FirstOrDefault(),

                    }).FirstOrDefault();

                return Ok(profile);
            }
        }

        [HttpPost]
        [Route("Profile")]
        public IHttpActionResult UpdateProfile(UserProfileViewModel profile)
        {
            using (var db = new KiwiEntities())
            {
                if (profile.Id == 0)
                {
                    return BadRequest("Invalid UserId");
                }
                else
                {
                    var contact = db.Users
                        .Where(x => x.Id == profile.Id)
                        .Select(x => x.Contact).FirstOrDefault();

                    if (contact == null)
                    {
                        return BadRequest("User Profile Not Found");
                    }

                    contact.FirstName = profile.FirstName;
                    contact.LastName = profile.LastName;
                    contact.GenderId = profile.GenderId;
                    contact.DateOfBirth = profile.DateOfBirth;

                    contact.Mobile = profile.Mobile;
                    contact.Landline = profile.Landline;
                    contact.Email = profile.Email;

                    contact.StreetNum = profile.StreetNum;
                    contact.Address = profile.Address;
                    contact.Suburb = profile.Suburb;
                    contact.City = profile.City;
                    contact.Postcode = profile.Postcode;
                    contact.Country = profile.Country;

                    contact.BillingStreetNum = profile.BillingStreetNum;
                    contact.BillingAddress = profile.BillingAddress;
                    contact.BillingSuburb = profile.BillingSuburb;
                    contact.BillingCity = profile.BillingCity;
                    contact.BillingPostcode = profile.BillingPostcode;
                    contact.BillingCountry = profile.BillingCountry;

                    contact.OscarNum = profile.OscarNum;
                    contact.ReviewDate = profile.ReviewDate;

                    db.SaveChanges();
                    return Ok(contact);
                }
            }
        }

        [HttpGet]
        [Route("Children")]
        public IHttpActionResult GetChildrenByUser()
        {
            var userId = User.Identity.GetUserId<int>();
            
            using (var db = new KiwiEntities())
            {
                
                var childList = db.User_Child
                    .Include(x => x.Child)
                    .Include(x => x.Child.Contact)
                    .Where(x => x.UserId == userId && x.Child.IsActive)
                    .AsEnumerable()
                    .Select(x => new
                    {
                        Id = x.ChildId,  // used to be x.Id, this is User_Child.Id
                        FirstName = x.Child.Contact.FirstName,
                        LastName = x.Child.Contact.LastName,
                        KnownName = x.Child.KnownName,
                        GenderId = x.Child.Contact.GenderId,
                        DateOfBirth = x.Child.Contact.DateOfBirth == null ? string.Empty : x.Child.Contact.DateOfBirth.Value.ToString(ApiConstants.DateFormat),
                        Image = db.Media
                            .Where(m => m.EntityId == x.ChildId
                                && m.EntityType == EntityTypeName.Child
                                && m.IsProfilePicture == true
                            )
                            .Select(m => m.NewFileName)
                            .FirstOrDefault()
                    }).ToList();

                if (childList == null || childList.Count == 0)
                {
                    return NotFound();
                }

                return Ok(childList);
            }
        }

        [HttpGet]
        [Route("Child")]
        public IHttpActionResult GetChild(int id)
        {
            using (var db = new KiwiEntities())
            {
                var childProfile = db.Children
                    .Include(x => x.Contact)
                    .Include(x => x.Child_Condition)
                    .Where(x => x.Id == id)
                    .AsEnumerable()
                    .Select(x => new
                    {
                        Id = x.Id,
                        FirstName = x.Contact.FirstName,
                        LastName = x.Contact.LastName,
                        KnownName = x.KnownName,
                        GenderId = x.Contact.GenderId,
                        DateOfBirth = x.Contact.DateOfBirth == null ? string.Empty : x.Contact.DateOfBirth.Value.ToString(ApiConstants.DateFormat),
                        
                        Image = db.Media
                            .Where(m => m.EntityId == x.Id
                                && m.EntityType == EntityTypeName.Child
                                && m.IsProfilePicture == true
                            )
                            .Select(m => m.NewFileName)
                            .FirstOrDefault()
                    }).FirstOrDefault();

                if (childProfile == null)
                {
                    return NotFound();
                }

                return Ok(childProfile);
            }
        }

        //[HttpPost]
        //[Route("Child")]
        //public IHttpActionResult UpdateChildProfile(Models.ApiModels.ChildProfileViewModel childProfile)
        //{
        //    using (var db = new KiwiEntities())
        //    {
        //        if (childProfile.Id == 0)
        //        {
        //            return BadRequest("Invalid UserId");
        //        }
        //        else
        //        {
        //            var contact = db.Children
        //                .Where(x => x.Id == childProfile.Id)
        //                .Select(x => x.Contact).FirstOrDefault();

        //            if (contact == null)
        //            {
        //                return BadRequest("Child Profile Not Found");
        //            }

        //            contact.FirstName = childProfile.FirstName;
        //            contact.LastName = childProfile.LastName;
        //            contact.GenderId = childProfile.GenderId;
        //            contact.DateOfBirth = childProfile.DateOfBirth;

        //            db.SaveChanges();

                    
        //            var child = db.Children
        //                .Where(x => x.Id == childProfile.Id).FirstOrDefault();
                        
        //            child.KnownName = childProfile.KnownName;
        //            child.Name = childProfile.FirstName + " " + childProfile.LastName;
                    
        //            db.SaveChanges();
        //            return Ok(contact);
        //        }
        //    }
        //}


        [HttpGet]
        [Route("ChildConditions")]
        public IHttpActionResult GetChildConditions(int id) {
            using(var db = new KiwiEntities())
	        {
		        var Conditions = db.Child_Condition
                    .Where(a => a.ChildId == id)
                    .Select(a => new
                    {
                        Id = a.Id,
                        Name = a.Condition.Name,
                        Treatment = a.TreatmentDesc,
                        DoctorName = a.DoctorName,
                        DoctorContact = a.DoctorContact,
                        MedicationDescription = a.MedicationDesc,
                        Severity = a.Severity,
                        Symptons = a.Symptoms,
                    }).ToList();

                return Ok(Conditions);
	        }
        }

        [HttpPost]
        [Route("Child")]
        public IHttpActionResult UpdateChild(Kiwi.Website.Portal.Models.ApiModels.ChildProfileViewModel childProfile)
        {
            var currentUsername = User.Identity.GetUserName();
            using (var db = new KiwiEntities())
            {
                if (childProfile.Id == 0)
                {
                    var contact = new Contact();
                    contact.FirstName = childProfile.FirstName;
                    contact.LastName = childProfile.LastName;
                    contact.GenderId = childProfile.GenderId;
                    contact.DateOfBirth = childProfile.DateOfBirth;
                    contact.IsActive = true;
                    contact.CreatedOn = DateTime.Now;
                    contact.CreatedBy = currentUsername;
                    contact.UpdatedOn = DateTime.Now;
                    contact.UpdatedBy = currentUsername;

                    db.Contacts.Add(contact);
                    db.SaveChanges();

                    var child = new Child();
                    child.ContactId = contact.Id;
                    child.Name = childProfile.FirstName + ' ' + childProfile.LastName;
                    child.KnownName = childProfile.KnownName;
                    child.IsActive = true;
                    child.CreatedOn = DateTime.Now;
                    child.CreatedBy = currentUsername;
                    child.UpdatedOn = DateTime.Now;
                    child.UpdatedBy = currentUsername;

                    db.Children.Add(child);
                    db.SaveChanges();
                    return Ok();
                }
                else
                {
                    var child = db.Children.Find(childProfile.Id);
                    if (child == null)
                    {
                        return BadRequest("Child Not Found");
                    }
                    child.Name = childProfile.FirstName + ' ' + childProfile.LastName;
                    child.KnownName = childProfile.KnownName;
                    child.IsActive = true;
                    child.UpdatedOn = DateTime.Now;
                    child.UpdatedBy = currentUsername;

                    var contact = child.Contact;
                    if (contact == null)
                    {
                        return BadRequest("Child Not Found");
                    }

                    contact.FirstName = childProfile.FirstName;
                    contact.LastName = childProfile.LastName;
                    contact.GenderId = childProfile.GenderId;
                    contact.DateOfBirth = childProfile.DateOfBirth;
                    contact.IsActive = true;
                    contact.UpdatedOn = DateTime.Now;
                    contact.UpdatedBy = currentUsername;

                    db.SaveChanges();
                    return Ok();
                }
            }

        }

        [HttpPost]
        [Route("ChildConditions")]
        public IHttpActionResult UpdateChildConditions(ChildMedicalConditionViewModel childProfile)
        {
            var currentUsername = User.Identity.GetUserName();
            using (var db = new KiwiEntities())
            {
                if (childProfile.Id == 0)
                {
                    var contact = new Contact();
                    contact.FirstName = childProfile.FirstName;
                    contact.LastName = childProfile.LastName;
                    contact.GenderId = childProfile.GenderId;
                    contact.DateOfBirth = childProfile.DateOfBirth;
                    contact.IsActive = true;
                    contact.CreatedOn = DateTime.Now;
                    contact.CreatedBy = currentUsername;
                    contact.UpdatedOn = DateTime.Now;
                    contact.UpdatedBy = currentUsername;

                    db.Contacts.Add(contact);
                    db.SaveChanges();

                    var child = new Child();
                    child.ContactId = contact.Id;
                    child.Name = childProfile.FirstName + ' ' + childProfile.LastName;
                    child.KnownName = childProfile.KnownName;
                    child.IsActive = true;
                    child.CreatedOn = DateTime.Now;
                    child.CreatedBy = currentUsername;
                    child.UpdatedOn = DateTime.Now;
                    child.UpdatedBy = currentUsername;

                    db.Children.Add(child);
                    db.SaveChanges();
                    return Ok();
                }
                else
                {
                    var child = db.Children.Find(childProfile.Id);
                    if (child == null)
                    {
                        return BadRequest("Child Not Found");
                    }
                    child.Name = childProfile.FirstName + ' ' + childProfile.LastName;
                    child.KnownName = childProfile.KnownName;
                    child.IsActive = true;
                    child.UpdatedOn = DateTime.Now;
                    child.UpdatedBy = currentUsername;

                    var contact = child.Contact;
                    if (contact == null)
                    {
                        return BadRequest("Child Not Found");
                    }

                    contact.FirstName = childProfile.FirstName;
                    contact.LastName = childProfile.LastName;
                    contact.GenderId = childProfile.GenderId;
                    contact.DateOfBirth = childProfile.DateOfBirth;
                    contact.IsActive = true;
                    contact.UpdatedOn = DateTime.Now;
                    contact.UpdatedBy = currentUsername;

                    db.SaveChanges();
                    return Ok();
                }
            }

        }

        [HttpGet]
        [Route("Attendances")]
        public IHttpActionResult GetAttendancesByUser(DateTime dateStart, DateTime dateEnd)
        {
            var userId = User.Identity.GetUserId<int>();
            using (var db = new KiwiEntities())
            {
                var attendances = db.Attendances
                    .Where(a => a.IsActive
                        && a.Booking_Spt.Booking.GuardianId == userId
                        && a.Booking_Spt.Booking.Status != "Pending"
                        && a.Day >= dateStart
                        && a.Day <= dateEnd
                    )
                    .AsEnumerable()
                    .Select(a => new
                    {
                        Id = a.Id,
                        Day = a.Day.ToString(ApiConstants.DateFormat),
                        BookedStart = a.BookedStart.ToString(ApiConstants.DateTimeFormat),
                        BookedEnd = a.BookedEnd.ToString(ApiConstants.DateTimeFormat),
                        ActualStart = a.ActualStart == null ? null : a.ActualStart.Value.ToString(ApiConstants.DateTimeFormat),
                        ActualEnd = a.ActualEnd == null ? null : a.ActualEnd.Value.ToString(ApiConstants.DateTimeFormat),
                        HasAttended = a.HasAttended,
                        RollCallStatus = a.RollCallStatus,
                        ProgramId = a.Booking_Spt.Site_Program_Term.Id,
                        ProgramTypeId = a.Booking_Spt.Site_Program_Term.ProgramCategory.TypeId,
                        ProgramName = a.Booking_Spt.Site_Program_Term.Name,
                        ProgramCode = a.Booking_Spt.Site_Program_Term.ProgramCategory.Code,
                        ChildId = a.ChildId,
                        ChildName = a.Child.Contact.FirstName + " " + a.Child.Contact.LastName,
                        SiteId = a.Booking_Spt.Booking.SiteId,
                        SiteName = a.Booking_Spt.Booking.Org.Name
                    }).ToList();

                return Ok(attendances);
            }
        }

        [HttpGet]
        [Route("Contacts")]
        public IHttpActionResult GetContactsByUser()
        {
            var userId = User.Identity.GetUserId<int>();
            using (var db = new KiwiEntities())
            {
                var parentContacts = db.User_Contact
                    .Where(x => x.UserId == userId
                        && x.Contact.IsActive
                        && x.Contact.FirstName != null
                        && x.Contact.LastName != null
                    )
                    .Select(x => new
                    {
                        Id = x.ContactId.Value,
                        Name = x.Contact.FirstName + " " + x.Contact.LastName,
                        Image = db.Media.Where(m => m.IsActive && m.EntityId == x.ContactId && m.EntityType == EntityTypeName.Contact).Select(m => m.NewFileName).FirstOrDefault(),
                        TypeId = x.ContactTypeId.Value,
                        Landline = x.Contact.Landline,
                        Office = x.Contact.Office,
                        Mobile = x.Contact.Mobile,

                    }).ToList();

                var childIds = db.User_Child
                    .Where(x => x.UserId == userId && x.Child.IsActive)
                    .Select(x => x.ChildId).ToList();

                var childContacts = db.Child_Contact
                    .Where(x => childIds.Contains(x.ChildId) && x.Contact.FirstName != null && x.Contact.LastName != null && x.Contact.IsActive)
                    .Select(x => new
                    {
                        Id = x.Contact.Id,
                        Name = x.Contact.FirstName + " " + x.Contact.LastName,
                        Image = db.Media.Where(m => m.IsActive && m.EntityId == x.ContactId && m.EntityType == EntityTypeName.Contact).Select(m => m.NewFileName).FirstOrDefault(),
                        TypeId = x.CanPickup == true ? 13 : 14,
                        Landline = x.Contact.Landline,
                        Office = x.Contact.Office,
                        Mobile = x.Contact.Mobile,
                    }).ToList();

                parentContacts.AddRange(childContacts);

                return Ok(parentContacts);
            }
        }

        [HttpGet]
        [Route("Invoices")]
        public IHttpActionResult GetInvoicesByUser()
        {
            //Draft = 54,
            //Submitted = 56,
            //Authorised = 57,
            //Deleted = 59,
            //Voided = 60,
            //Confirmed = 1093,
            //Error = 1095,
            //Paid = 1096,
            //Approved = 1151
            var userId = User.Identity.GetUserId<int>();
            var validStatus = new int[] { 57, 1096, 1151 };
            using (var db = new KiwiEntities())
            {
                var invoices = db.Invoices
                    .Where(x => x.Billing.IsActive
                        && x.IsActive
                        && validStatus.Contains(x.StatusId)
                        && x.Billing.User.Id == userId
                    )
                    .AsEnumerable()
                    .Select(x => new
                    {
                        Id = x.Id,
                        UserId = x.Billing.UserId,
                        SiteId = x.Billing.Org.Id,
                        SiteName = x.Billing.Org.Name,

                        XeroInvoiceCode = x.XeroInvoiceCode,
                        StatusId = x.StatusId,
                        Reference = x.Reference,
                        AmountDue = x.AmountDue,
                        TotalAmount = x.TotalAmount,
                        CreatedOn = x.CreatedOn.ToString(ApiConstants.DateTimeFormat),
                        DueDate = x.DueDate.ToString(ApiConstants.DateTimeFormat),                                                                 
                    }).ToList();

                return Ok(invoices);
            }           
        }

        [HttpGet]
        [Route("Invoice")]
        public IHttpActionResult GetInvoice(int? id)
        {
            using (var db = new KiwiEntities())
            {
                var invoice = db.InvoiceLines
                    .Include(x => x.Invoice.Billing)
                    .Include(x => x.Invoice.Billing.Org)
                    .Include(x => x.Booking_Spt.Booking.Child)
                    .Include(x => x.Booking_Spt.Site_Program_Term)
                    .Where(x => x.IsActive
                        && x.Invoice.IsActive
                        && x.Invoice.Id == id
                    )
                    .AsEnumerable()
                    .GroupBy(x => x.Invoice)
                    .Select(g => new
                    {
                        Id = g.Key.Id,
                        UserId = g.Key.Billing.UserId,
                        SiteId = g.Key.Billing.Org.Id,
                        SiteName = g.Key.Billing.Org.Name,

                        XeroInvoiceCode = g.Key.XeroInvoiceCode,
                        StatusId = g.Key.StatusId,
                        Reference = g.Key.Reference,
                        AmountDue = g.Key.AmountDue,
                        TotalAmount = g.Key.TotalAmount,
                        CreatedOn = g.Key.CreatedOn.ToString(ApiConstants.DateTimeFormat),
                        DueDate = g.Key.DueDate.ToString(ApiConstants.DateFormat),

                        InvoiceLines = g.Select(l => new
                        {
                            Id = l.Id,
                            ChildName = l.Booking_Spt.Booking.Child.Name,
                            ProgrameName = l.Booking_Spt.Site_Program_Term.Name,
                            UnitPrice = l.UnitPrice,
                            Quantity = l.Quantity,
                            Amount = l.Amount,
                            Description = l.Description,
                        }).ToList()
                    }).FirstOrDefault();

                if (invoice == null)
                {
                    return NotFound();
                }
                
                return Ok(invoice);
            }
        }
    }
}